package pl.mrstudios.proxy.core.connection.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.core.connection.ConnectionCredentials;
import pl.mrstudios.proxy.core.connection.RemoteConnection;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.impl.RemotePacketReceivedEvent;
import pl.mrstudios.proxy.netty.codec.NettyCompressionCodec;
import pl.mrstudios.proxy.netty.codec.NettyFrameCodec;
import pl.mrstudios.proxy.netty.codec.NettyPacketCodec;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;
import pl.mrstudios.proxy.netty.packet.impl.UndefinedPacket;
import pl.mrstudios.proxy.netty.packet.impl.handshake.HandshakePacket;
import pl.mrstudios.proxy.netty.packet.impl.login.client.ClientLoginStartPacket;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginDisconnectPacket;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginSetCompressionPacket;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginSuccessPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientKeepAlivePacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientResourcePackResponsePacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.*;

import java.net.SocketException;
import java.util.Objects;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;
import static io.netty.channel.ChannelOption.IP_TOS;
import static io.netty.channel.ChannelOption.TCP_NODELAY;
import static io.netty.channel.epoll.Epoll.isAvailable;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.gc;
import static java.net.Proxy.NO_PROXY;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Stream.of;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static pl.mrstudios.proxy.minecraft.player.ResourcePackAction.LOADED;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.LOGIN;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.PLAY;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.CLIENT;
import static pl.mrstudios.proxy.util.ProtocolUtil.dimensionSwitch;
import static pl.mrstudios.proxy.util.ReflectUtil.callVoid;
import static pl.mrstudios.proxy.util.StringUtil.removeLegacyColors;

@SuppressWarnings({ "unused", "FieldCanBeLocal" })
public class RemoteServerConnection implements RemoteConnection {

    public final User user;
    public final String name;
    private final ProxyEntry proxyEntry;
    public final UserManager userManager;
    private final EventManager eventManager;
    private final PacketRegistry packetRegistry;
    public final ConnectionCredentials credentials;


    /* Connection */
    public Channel channel;
    public final EventLoopGroup eventLoopGroup;

    /* Data */
    public Packet lastReceivedPacket;
    public long lastReceivedPacketTime;
    public boolean ignoreDisconnection;
    public boolean notifiedDisconnected;

    public RemoteServerConnection(
            @NotNull User user,
            @NotNull String name,
            @NotNull ProxyEntry proxyEntry,
            @NotNull UserManager userManager,
            @NotNull EventManager eventManager,
            @NotNull PacketRegistry packetRegistry,
            @NotNull ConnectionCredentials credentials
    ) {

        this.user = user;
        this.name = name;
        this.proxyEntry = proxyEntry;
        this.userManager = userManager;
        this.credentials = credentials;
        this.eventManager = eventManager;
        this.packetRegistry = packetRegistry;

        this.ignoreDisconnection = false;
        this.notifiedDisconnected = false;

        RemoteServerStatusResolver remoteServerStatusResolver = new RemoteServerStatusResolver(user, credentials, proxyEntry, packetRegistry);

        while (remoteServerStatusResolver.isConnected())
            callVoid();

        this.eventLoopGroup = (isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup());

        try {

            this.channel = new Bootstrap()
                    .group(this.eventLoopGroup)
                    .channel((isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class))
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(@NotNull SocketChannel socketChannel) {

                            if (proxyEntry.proxy() != NO_PROXY)
                                switch (proxyEntry.proxy().type()) {

                                    case HTTP ->
                                            socketChannel.pipeline().addFirst(new HttpProxyHandler(proxyEntry.proxy().address()));

                                    case SOCKS ->
                                            socketChannel.pipeline().addFirst(new Socks5ProxyHandler(proxyEntry.proxy().address()));

                                    default -> {}

                                }

                            socketChannel.config().setOption(IP_TOS, 0x18);
                            socketChannel.config().setOption(TCP_NODELAY, true);
                            socketChannel.pipeline().addLast("timer", new ReadTimeoutHandler(30));
                            socketChannel.pipeline().addLast("frame-codec", new NettyFrameCodec());
                            socketChannel.pipeline().addLast("packet-codec", new NettyPacketCodec(
                                    packetRegistry, user.getConnection().getMinecraftVersion(), LOGIN, CLIENT
                            ));
                            socketChannel.pipeline().addLast("handler", new SimpleChannelInboundHandler<Packet>() {

                                @Override
                                public void channelActive(@NotNull ChannelHandlerContext ctx) {
                                    sendPacket(new HandshakePacket(
                                            user.getConnection().getProtocol(),
                                            credentials.host(), ofNullable(credentials.port()).orElse(25565),
                                            LOGIN.getId())
                                    );
                                    sendPacket(new ClientLoginStartPacket(name, null));
                                    userManager.users()
                                            .forEach((target) -> target.sendMessage(
                                                    target.getLanguage().proxyRemoteJoinMessageFormat, user.getName(),
                                                    user.getAccount().getSettings().incognito ? String.format("<obfuscated>%s</obfuscated>", random(name.length())) : name,
                                                    user.getAccount().getSettings().incognito ? String.format("<obfuscated>%s</obfuscated>", random(credentials.toString().length())) : credentials.toString()
                                            ));
                                }

                                @Override
                                public void channelInactive(@NotNull ChannelHandlerContext ctx) {
                                    if (!ignoreDisconnection)
                                        disconnect(null, true);
                                }

                                @Override
                                protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull Packet object) {

                                    if (user.getRemoteConnection() != RemoteServerConnection.this) {
                                        ignoreDisconnection = true;
                                        ctx.channel().close();
                                        return;
                                    }

                                    lastReceivedPacket = object;
                                    lastReceivedPacketTime = currentTimeMillis();

                                    eventManager.call(new RemotePacketReceivedEvent(
                                            user, object, RemoteServerConnection.this
                                    ));

                                    if (object instanceof ServerLoginSetCompressionPacket packet)
                                        ofNullable(ctx.channel().pipeline().get(NettyCompressionCodec.class))
                                                .orElseGet(
                                                        () -> ctx.channel().pipeline()
                                                                .addBefore("packet-codec", "compression-codec", new NettyCompressionCodec(packet.getThreshold()))
                                                                .get(NettyCompressionCodec.class)
                                                ).setCompressionThreshold(packet.getThreshold());

                                    else if (object instanceof ServerLoginSuccessPacket)
                                        ctx.channel().pipeline().get(NettyPacketCodec.class)
                                                .setConnectionState(PLAY);

                                    else if (object instanceof ServerJoinGamePacket packet)
                                        dimensionSwitch(user, packet);

                                    else if (object instanceof ServerDisconnectPacket packet)
                                        disconnect(packet, false);

                                    else if (object instanceof ServerLoginDisconnectPacket packet)
                                        disconnect(packet, false);

                                    else if (object instanceof ServerKeepAlivePacket packet)
                                        sendPacket(new ClientKeepAlivePacket(packet.getKeepAliveId()));

                                    else if (object instanceof ServerPluginMessagePacket packet && (packet.getChannel().equals("minecraft:brand") || packet.getChannel().equals("MC|Brand")))
                                        user.sendMessage(user.getLanguage().serviceConnectResolvedServerBrand, removeLegacyColors(new String(packet.getData(), UTF_8).substring(1)));

                                    else if (object instanceof ServerPlayerListHeaderAndFooterPacket)
                                        callVoid();

                                    else if (object instanceof UndefinedPacket packet && packet.getId() == resourcePackPacketIdOf(user.getConnection().getMinecraftVersion()) && user.getAccount().getSettings().blockResourcePacks)
                                        sendPacket(new ClientResourcePackResponsePacket(LOADED));

                                    else
                                        user.getConnection().sendPacket(object);

                                }

                                @Override
                                public void exceptionCaught(@NotNull ChannelHandlerContext ctx, @NotNull Throwable throwable) {

                                    if (
                                            of(
                                                    ReadTimeoutException.class,
                                                    SocketException.class
                                            ).noneMatch((clazz) -> clazz == throwable.getClass())
                                    ) return;

                                    user.sendMessage(user.getLanguage().errorExceptionOccurredYourConnection, credentials.toString());
                                    ctx.channel().close();
                                }

                            });

                        }

                    })
                    .connect(this.credentials.host(), ofNullable(this.credentials.port()).orElse(25565))
                    .syncUninterruptibly().channel();


        } catch (@NotNull Exception exception) {

            this.user.sendMessage(
                    this.user.getLanguage().errorExceptionOccurredYourConnection,
                    credentials.toString()
            );

            this.user.setRemoteConnection(null);
            this.eventLoopGroup.shutdownGracefully();

        }

    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        this.channel.writeAndFlush(packet).addListener(CLOSE_ON_FAILURE);
    }

    @Override
    public boolean isConnected() {
        return ofNullable(this.channel)
                .map(Channel::isActive)
                .orElse(false);
    }

    @Override
    public void disconnect() {
        this.channel.close();
        this.eventLoopGroup.shutdownGracefully();
    }

    @Override
    public void disconnect(@Nullable Packet object, boolean silent) {

        try {

            ofNullable(this.user.getScheduledFuture())
                    .ifPresent((future) -> future.cancel(true));

            this.user.setRemoteConnection(null);
            this.user.getBots().stream().filter(Objects::nonNull)
                    .filter((bot) -> bot.connection().isConnected())
                    .filter((bot) -> bot.connection() instanceof RemoteServerBotConnection)
                    .forEach((bot) -> bot.connection().disconnect(null, true));
            this.user.getBots().removeIf((bot) -> bot.connection() instanceof RemoteServerBotConnection);

            this.eventLoopGroup.shutdownGracefully();
            this.channel.close();
            this.user.lobby(true);
            this.channel = null;

            if (this.notifiedDisconnected)
                return;

            this.notifiedDisconnected = true;
            this.userManager.users()
                    .forEach((target) -> target.sendMessage(
                            target.getLanguage().proxyRemoteServerDisconnectMessageFormat, this.user.getName(),
                            this.user.getAccount().getSettings().incognito ?
                                    String.format("<obfuscated>%s</obfuscated>", random(this.name.length())) : this.name,
                            this.user.getAccount().getSettings().incognito ?
                                    String.format("<obfuscated>%s</obfuscated>", random(this.credentials.toString().length())) : this.credentials.toString()
                    ));

            if (!silent)
                ofNullable(object)
                        .ifPresentOrElse(
                                (packet) -> {

                                    if (packet instanceof ServerLoginDisconnectPacket disconnectPacket)
                                        notifyDisconnected(disconnectPacket.getReason());

                                    else if (packet instanceof ServerDisconnectPacket disconnectPacket)
                                        notifyDisconnected(disconnectPacket.getComponent());

                                    else
                                        this.user.sendMessage(
                                                this.user.getLanguage().errorExceptionOccurredYourConnection,
                                                credentials.toString()
                                        );

                                    if (!this.user.getAccount().getSettings().autoReconnectPlayer)
                                        return;

                                    this.user.sendMessage(this.user.getLanguage().autoReconnectService, this.user.getAccount().getSettings().autoReconnectPlayerDelay);
                                    newSingleThreadScheduledExecutor()
                                            .schedule(
                                                    () -> {

                                                        if (!this.user.isConnected())
                                                            return;

                                                        this.user.setRemoteConnection(new RemoteServerConnection(
                                                                this.user, this.name, this.proxyEntry, this.userManager,
                                                                this.eventManager, this.packetRegistry, this.credentials
                                                        ));

                                                    }, this.user.getAccount().getSettings().autoReconnectPlayerDelay, MILLISECONDS
                                            );

                                }, () -> this.user.sendMessage(
                                        this.user.getLanguage().errorExceptionOccurredYourConnection,
                                        credentials.toString()
                                )
                        );

        } catch (Exception ignored) {}
        gc();

    }

    protected void notifyDisconnected(@NotNull Component component) {
        this.user.sendMessage(
                this.user.getLanguage().proxyRemoteDisconnectedMessageFormat,
                credentials.toString(),
                removeLegacyColors(plainText().serialize(component))
                        .replace("\\n", "<br>")
        );
    }

    protected static int resourcePackPacketIdOf(@NotNull MinecraftVersion minecraftVersion) {
        return switch (minecraftVersion) {

            case MINECRAFT_1_16_5 -> 0x38;
            case MINECRAFT_1_18_2 -> 0x3C;
            case MINECRAFT_1_19_4, MINECRAFT_1_20_1 -> 0x40;

            default -> 0xffffff;

        };
    }

}
