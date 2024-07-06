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
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.core.connection.ConnectionCredentials;
import pl.mrstudios.proxy.core.connection.RemoteConnection;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.entity.Bot;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.impl.RemoteBotPacketReceivedEvent;
import pl.mrstudios.proxy.netty.codec.NettyCompressionCodec;
import pl.mrstudios.proxy.netty.codec.NettyFrameCodec;
import pl.mrstudios.proxy.netty.codec.NettyPacketCodec;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;
import pl.mrstudios.proxy.netty.packet.impl.handshake.HandshakePacket;
import pl.mrstudios.proxy.netty.packet.impl.login.client.ClientLoginStartPacket;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginDisconnectPacket;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginSetCompressionPacket;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginSuccessPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientKeepAlivePacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientPluginMessagePacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientPongPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientSettingsPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.*;

import static io.netty.buffer.Unpooled.buffer;
import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;
import static io.netty.channel.ChannelOption.IP_TOS;
import static io.netty.channel.ChannelOption.TCP_NODELAY;
import static io.netty.channel.epoll.Epoll.isAvailable;
import static java.net.Proxy.NO_PROXY;
import static java.util.Optional.ofNullable;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.LOGIN;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.PLAY;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.CLIENT;
import static pl.mrstudios.proxy.util.StreamUtil.writeString;
import static pl.mrstudios.proxy.util.StringUtil.removeLegacyColors;

@Getter @Setter
@SuppressWarnings({ "unused", "FieldCanBeLocal" })
public class RemoteServerBotConnection implements RemoteConnection {

    public final Bot bot;
    public final ConnectionCredentials credentials;

    /* Connection */
    public Channel channel;
    private final EventLoopGroup eventLoopGroup;

    /* Data */
    private boolean connected;
    private boolean notifiedDisconnected;

    public RemoteServerBotConnection(
            @NotNull User user,
            @NotNull String name,
            @NotNull ProxyEntry proxyEntry,
            @NotNull EventManager eventManager,
            @NotNull PacketRegistry packetRegistry,
            @NotNull ConnectionCredentials credentials
    ) {

        this.bot = new Bot(user, name, this);
        this.connected = false;
        this.credentials = credentials;
        this.notifiedDisconnected = false;
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
                                    packetRegistry, bot.user().getConnection().getMinecraftVersion(), LOGIN, CLIENT
                            ));
                            socketChannel.pipeline().addLast("handler", new SimpleChannelInboundHandler<Packet>() {

                                @Override
                                public void channelActive(@NotNull ChannelHandlerContext ctx) {

                                    sendPacket(new HandshakePacket(
                                            bot.user().getConnection().getProtocol(), credentials.host(),
                                            ofNullable(credentials.port()).orElse(25565), LOGIN.getId())
                                    );
                                    sendPacket(new ClientLoginStartPacket(name, null));

                                }

                                @Override
                                public void channelInactive(@NotNull ChannelHandlerContext ctx) {
                                    if (connected)
                                        disconnect(null, true);
                                }

                                @Override
                                protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull Packet object) {

                                    eventManager.call(new RemoteBotPacketReceivedEvent(bot, object));

                                    if (object instanceof ServerLoginSetCompressionPacket packet)
                                        ofNullable(ctx.channel().pipeline().get(NettyCompressionCodec.class))
                                                .orElseGet(
                                                        () -> ctx.channel().pipeline()
                                                                .addBefore("packet-codec", "compression-codec", new NettyCompressionCodec(packet.getThreshold()))
                                                                .get(NettyCompressionCodec.class)
                                                ).setCompressionThreshold(packet.getThreshold());

                                    else if (object instanceof ServerLoginSuccessPacket) {
                                        ctx.channel().pipeline().get(NettyPacketCodec.class)
                                                .setConnectionState(PLAY);
                                        bot.user().getBots().add(bot);
                                        if (user.getAccount().getSettings().displayBotInfo)
                                            bot.user().sendMessage(bot.user().getLanguage().remoteBotInfoBotConnectedMessageFormat, bot.name(), credentials.toString());
                                    }

                                    else if (object instanceof ServerJoinGamePacket packet) {

                                        sendPacket(new ClientSettingsPacket(
                                                "en_GB",
                                                (byte) 8,
                                                0,
                                                true,
                                                (byte) 0x80,
                                                1,
                                                true,
                                                true
                                        ));
                                        sendPacket(new ClientPluginMessagePacket("minecraft:brand", writeString(buffer(), "vanilla")));

                                        if (!connected && bot.user().getAccount().getSettings().displayBotInfo) {
                                            bot.user().sendMessage(bot.user().getLanguage().remoteBotInfoBotSwitchedServerMessageFormat, bot.name());
                                            connected = true;
                                        }

                                    }

                                    else if (object instanceof ServerDisconnectPacket packet)
                                        disconnect(packet, false);

                                    else if (object instanceof ServerLoginDisconnectPacket packet)
                                        disconnect(packet, false);

                                    else if (object instanceof ServerKeepAlivePacket packet)
                                        sendPacket(new ClientKeepAlivePacket(packet.getKeepAliveId()));

                                    else if (object instanceof ServerPingPacket packet)
                                        sendPacket(new ClientPongPacket(packet.getPingId()));

                                    else if (object instanceof ServerChatMessagePacket packet)
                                        if (bot.user().getAccount().getSettings().displayBotChat)
                                            bot.user().sendMessage(
                                                    bot.user().getLanguage().remoteBotChatReceivedMessageFormat, bot.name(),
                                                    removeLegacyColors(plainText().serialize(packet.getMessage()))
                                            );

                                }

                                @Override
                                public void exceptionCaught(@NotNull ChannelHandlerContext ctx, @NotNull Throwable throwable) {
                                    disconnect(null, false);
                                    ctx.channel().close();
                                }

                            });

                        }

                    })
                    .connect(this.credentials.host(), ofNullable(this.credentials.port()).orElse(25565))
                    .syncUninterruptibly().channel();


        } catch (@NotNull Exception exception) {
            disconnect(null, true);
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
        this.connected = false;
        this.channel.close();
        this.eventLoopGroup.shutdownGracefully();
    }

    @Override
    public void disconnect(@Nullable Packet object, boolean silent) {

        try {

            this.eventLoopGroup.shutdownGracefully();
            this.channel.close();
            this.channel = null;

            synchronized (this.bot.user().getBots()) {
                this.bot.user().getBots().remove(this.bot);
            }

            if (this.notifiedDisconnected)
                return;

            this.notifiedDisconnected = true;
            if (!this.bot.user().getAccount().getSettings().displayBotInfo)
                return;

            if (!silent)
                ofNullable(object)
                        .ifPresentOrElse(
                                (packet) -> {

                                    if (packet instanceof ServerLoginDisconnectPacket disconnectPacket)
                                        notifyDisconnected(disconnectPacket.getReason());

                                    else if (packet instanceof ServerDisconnectPacket disconnectPacket)
                                        notifyDisconnected(disconnectPacket.getComponent());

                                }, () -> notifyDisconnected(Component.text("Disconnected"))
                        );

        } catch (Exception ignored) {}

    }

    protected void notifyDisconnected(@NotNull Component component) {
        this.bot.user().sendMessage(
                this.bot.user().getLanguage().remoteBotInfoBotDisconnectedMessageFormat,
                "<dark_aqua>" + removeLegacyColors(plainText().serialize(component))
                        .replace("\\n", "<br>"),
                this.bot.name(), this.credentials.toString()
        );
    }

}
