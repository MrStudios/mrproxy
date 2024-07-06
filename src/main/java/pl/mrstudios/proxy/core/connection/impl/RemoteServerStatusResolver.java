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
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.connection.ConnectionCredentials;
import pl.mrstudios.proxy.core.connection.RemoteConnection;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.netty.codec.NettyFrameCodec;
import pl.mrstudios.proxy.netty.codec.NettyPacketCodec;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;
import pl.mrstudios.proxy.netty.packet.impl.handshake.HandshakePacket;
import pl.mrstudios.proxy.netty.packet.impl.status.client.ClientStatusPingPacket;
import pl.mrstudios.proxy.netty.packet.impl.status.client.ClientStatusRequestPacket;
import pl.mrstudios.proxy.netty.packet.impl.status.server.ServerStatusPongPacket;
import pl.mrstudios.proxy.netty.packet.impl.status.server.ServerStatusResponsePacket;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;
import static io.netty.channel.ChannelOption.IP_TOS;
import static io.netty.channel.ChannelOption.TCP_NODELAY;
import static io.netty.channel.epoll.Epoll.isAvailable;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.net.Proxy.NO_PROXY;
import static java.util.List.of;
import static java.util.Optional.ofNullable;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.STATUS;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.CLIENT;
import static pl.mrstudios.proxy.util.StringUtil.removeLegacyColors;

@SuppressWarnings({ "unused", "FieldCanBeLocal" })
public class RemoteServerStatusResolver implements RemoteConnection {

    private final User user;

    /* Connection */
    private Channel channel;
    private final ProxyEntry proxyEntry;
    private final EventLoopGroup eventLoopGroup;

    /* Packets */
    private ServerStatusPongPacket pongPacket;
    private ServerStatusResponsePacket responsePacket;

    public RemoteServerStatusResolver(
            @NotNull User user,
            @NotNull ConnectionCredentials credentials,
            @NotNull ProxyEntry proxyEntry,
            @NotNull PacketRegistry packetRegistry
    ) {

        this.user = user;
        this.proxyEntry = proxyEntry;
        this.eventLoopGroup = (isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup());

        Bootstrap bootstrap = new Bootstrap()
                .group(this.eventLoopGroup)
                .channel((isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class))
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(@NotNull SocketChannel socketChannel) {

                        if (RemoteServerStatusResolver.this.proxyEntry.proxy() != NO_PROXY)
                            switch (RemoteServerStatusResolver.this.proxyEntry.proxy().type()) {

                                case HTTP ->
                                        socketChannel.pipeline().addFirst(new HttpProxyHandler(RemoteServerStatusResolver.this.proxyEntry.proxy().address()));

                                case SOCKS ->
                                        socketChannel.pipeline().addFirst(new Socks5ProxyHandler(RemoteServerStatusResolver.this.proxyEntry.proxy().address()));

                                default -> {}

                            }

                        socketChannel.config().setOption(IP_TOS, 0x18);
                        socketChannel.config().setOption(TCP_NODELAY, true);
                        socketChannel.pipeline().addLast("timer", new ReadTimeoutHandler(10));
                        socketChannel.pipeline().addLast("frame-codec", new NettyFrameCodec());
                        socketChannel.pipeline().addLast("packet-codec", new NettyPacketCodec(
                                packetRegistry,
                                RemoteServerStatusResolver.this.user.getConnection().getMinecraftVersion(),
                                STATUS, CLIENT
                        ));
                        socketChannel.pipeline().addLast("handler", new SimpleChannelInboundHandler<Packet>() {

                            @Override
                            public void channelActive(@NotNull ChannelHandlerContext ctx) {
                                of(
                                        new HandshakePacket(
                                                RemoteServerStatusResolver.this.user.getConnection().getProtocol(),
                                                credentials.host(), ofNullable(credentials.port()).orElse(25565),
                                                STATUS.getId()
                                        ),
                                        new ClientStatusRequestPacket(),
                                        new ClientStatusPingPacket(currentTimeMillis())
                                ).forEach((packet) -> ctx.channel().writeAndFlush(packet).addListener(CLOSE_ON_FAILURE));
                            }

                            @Override
                            public void channelInactive(@NotNull ChannelHandlerContext ctx) {
                                RemoteServerStatusResolver.this.eventLoopGroup.shutdownGracefully();
                            }

                            @Override
                            protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull Packet object) {

                                if (object instanceof ServerStatusPongPacket packet)
                                    pongPacket = packet;

                                if (object instanceof ServerStatusResponsePacket packet)
                                    responsePacket = packet;

                                if (pongPacket == null || responsePacket == null)
                                    return;

                                RemoteServerStatusResolver.this.user.sendMessage(removeLegacyColors(format(
                                        RemoteServerStatusResolver.this.user.getLanguage().serverStatusResolverResponse,
                                        (currentTimeMillis() - pongPacket.getPing()),
                                        responsePacket.getServerInfo().getVersion().getName(), responsePacket.getServerInfo().getVersion().getProtocol(),
                                        responsePacket.getServerInfo().getPlayers().getOnline(), responsePacket.getServerInfo().getPlayers().getMax(),
                                        plainText().serialize(responsePacket.getServerInfo().getDescription())
                                )));
                                ctx.channel().close();

                            }

                            @Override
                            public void exceptionCaught(@NotNull ChannelHandlerContext ctx, @NotNull Throwable throwable) {
                                RemoteServerStatusResolver.this.user.sendMessage(RemoteServerStatusResolver.this.user.getLanguage().serverStatusResolverConnectionFailed);
                                ctx.channel().close();
                            }

                        });

                    }

                });

        try {
            this.channel = bootstrap.connect(credentials.host(), ofNullable(credentials.port()).orElse(25565))
                    .syncUninterruptibly().channel();
        } catch (Exception exception) {
            this.user.sendMessage(this.user.getLanguage().serverStatusResolverConnectionFailed);
        }

    }

    @Override
    public void disconnect() {
        this.channel.close();
        this.eventLoopGroup.shutdownGracefully();
    }

    @Override
    public boolean isConnected() {
        return ofNullable(this.channel)
                .map(Channel::isActive)
                .orElse(false);
    }

}
