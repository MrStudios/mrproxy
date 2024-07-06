package pl.mrstudios.proxy.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.Injector;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.commons.reflection.Reflections;
import pl.mrstudios.proxy.core.config.Configuration;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.impl.ProxyPacketReceivedEvent;
import pl.mrstudios.proxy.event.impl.ProxyUserDisconnectedEvent;
import pl.mrstudios.proxy.logger.Logger;
import pl.mrstudios.proxy.netty.codec.NettyFrameCodec;
import pl.mrstudios.proxy.netty.codec.NettyPacketCodec;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.listener.PacketListener;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerKeepAlivePacket;

import java.util.*;
import java.util.concurrent.Executors;

import static io.netty.channel.ChannelOption.SO_KEEPALIVE;
import static io.netty.channel.ChannelOption.TCP_NODELAY;
import static io.netty.channel.epoll.Epoll.isAvailable;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.lineSeparator;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.HANDSHAKE;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.PLAY;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.UNKNOWN;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.SERVER;
import static pl.mrstudios.proxy.util.StringUtil.throwableToString;

@Getter
public class NettyServer {

    private final Logger logger;
    private final Injector injector;
    private final UserManager userManager;
    private final EventManager eventManager;
    private final Configuration configuration;

    /* Networking */
    private ChannelFuture channelFuture;
    private final EventLoopGroup eventLoopGroup;
    private final PacketRegistry packetRegistry;

    /* Collections */
    private final Collection<Connection> connections;
    private final Map<Class<? extends Packet>, Collection<PacketListener<Packet>>> packetListeners;

    @Inject
    public NettyServer(
            @NotNull Logger logger,
            @NotNull Injector injector,
            @NotNull UserManager userManager,
            @NotNull EventManager eventManager,
            @NotNull Configuration configuration,
            @NotNull PacketRegistry packetRegistry
    ) {

        this.logger = logger;
        this.injector = injector;
        this.userManager = userManager;
        this.eventManager = eventManager;
        this.configuration = configuration;

        /* Networking */
        this.packetRegistry = packetRegistry;
        this.eventLoopGroup = isAvailable() ?
                new EpollEventLoopGroup(this.coreCount) : new NioEventLoopGroup(this.coreCount);

        /* Collections */
        this.connections = new ArrayList<>();
        this.packetListeners = new HashMap<>();

        /* Run */
        this.listeners();
        this.run();

    }

    protected void listeners() {

        new Reflections<PacketListener<Packet>>("pl.mrstudios.proxy")
                .getClassesImplementing(PacketListener.class)
                .stream().map(this.injector::inject)
                .filter(Objects::nonNull).forEach((listener) -> {
                    this.packetListeners.computeIfAbsent(listener.listeningPacket(), (key) -> new ArrayList<>());
                    this.packetListeners.get(listener.listeningPacket()).add(listener);
                });

    }

    protected void run() {

        this.channelFuture = new ServerBootstrap()
                .channel(isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(this.eventLoopGroup)
                .childOption(TCP_NODELAY, true)
                .childOption(SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) {

                        Connection connection = new Connection(socketChannel);
                        if (NettyServer.this.configuration.server.behindHaProxy)
                            socketChannel.pipeline().addFirst("proxy-protocol", new HAProxyMessageDecoder());

                        socketChannel.pipeline().addLast("timer", new ReadTimeoutHandler(40));
                        socketChannel.pipeline().addLast("frame-codec", new NettyFrameCodec());
                        socketChannel.pipeline().addLast("packet-codec", new NettyPacketCodec(
                                NettyServer.this.packetRegistry,
                                UNKNOWN, HANDSHAKE, SERVER
                        ));

                        socketChannel.pipeline().addLast("handler", new SimpleChannelInboundHandler<Packet>() {

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                NettyServer.this.connections.add(connection);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) {

                                NettyServer.this.connections.remove(connection);
                                if (connection.getConnectionState() != PLAY)
                                    return;

                                NettyServer.this.logger.info("%s (%s) [%s] disconnected from proxy.", connection.getGameProfile().getName(), ctx.channel().remoteAddress().toString(), connection.getMinecraftVersion().getName());
                                NettyServer.this.eventManager.call(new ProxyUserDisconnectedEvent(
                                        NettyServer.this.userManager.users()
                                                .stream()
                                                .filter((user) -> user.getConnection() == connection)
                                                .findFirst()
                                                .orElseThrow()
                                ));

                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {

                                ofNullable(NettyServer.this.packetListeners.get(packet.getClass()))
                                        .orElse(emptyList()).forEach((listener) -> listener.handle(connection, packet));

                                NettyServer.this.eventManager.call(new ProxyPacketReceivedEvent(connection, packet));

                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
                                NettyServer.this.getLogger().error(
                                        "Uncaught exception in %s connection.%s%s",
                                        connection.getAddress().toString(),
                                        lineSeparator(), throwableToString(throwable)
                                );
                            }

                        });

                    }

                })
                .bind(this.configuration.server.host, this.configuration.server.port)
                .addListener((channel) -> {

                    if (channel.isSuccess()) {
                        this.logger.info("Server is now listening on %s:%d.", this.configuration.server.host, this.configuration.server.port);
                        return;
                    }

                    throw new RuntimeException("Unable to start application on 25565 due to exception.", channel.cause());

                });

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(
                        () -> this.connections.stream()
                                .filter((connection) -> connection.getConnectionState() == PLAY)
                                .forEach((connection) -> connection.sendPacket(new ServerKeepAlivePacket((int) currentTimeMillis()))),
                        0, 5, SECONDS
                );

    }

    protected final int coreCount = Runtime.getRuntime().availableProcessors();

}
