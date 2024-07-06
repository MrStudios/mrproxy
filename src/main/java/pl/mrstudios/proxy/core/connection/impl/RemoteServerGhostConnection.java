package pl.mrstudios.proxy.core.connection.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.core.connection.ConnectionCredentials;
import pl.mrstudios.proxy.core.connection.RemoteConnection;
import pl.mrstudios.proxy.core.user.entity.Bot;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.impl.RemoteBotPacketReceivedEvent;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginDisconnectPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientKeepAlivePacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientPluginMessagePacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientPongPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientSettingsPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.*;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;
import static java.util.Optional.ofNullable;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;
import static pl.mrstudios.proxy.util.StreamUtil.writeString;
import static pl.mrstudios.proxy.util.StringUtil.removeLegacyColors;

@Getter @Setter
@SuppressWarnings({ "unused", "FieldCanBeLocal" })
public class RemoteServerGhostConnection implements RemoteConnection {

    public final Bot bot;
    public final ConnectionCredentials credentials;

    /* Connection */
    public Channel channel;
    public final EventLoopGroup eventLoopGroup;

    /* Data */
    private boolean connected;
    private boolean notifiedDisconnected;

    public RemoteServerGhostConnection(
            @NotNull EventManager eventManager,
            @NotNull RemoteServerConnection connection
    ) {

        this.connected = true;
        this.notifiedDisconnected = false;
        this.channel = connection.channel;
        this.credentials = connection.credentials;
        this.eventLoopGroup = connection.eventLoopGroup;
        this.bot = new Bot(connection.user, connection.name, this);
        this.bot.user().getBots().add(this.bot);

        try {

            this.channel.pipeline().replace("handler", "handler", new SimpleChannelInboundHandler<Packet>() {

                @Override
                public void channelActive(@NotNull ChannelHandlerContext ctx) {}

                @Override
                public void channelInactive(@NotNull ChannelHandlerContext ctx) {
                    if (connected)
                        disconnect(null, true);
                }

                @Override
                protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull Packet object) {

                    eventManager.call(new RemoteBotPacketReceivedEvent(bot, object));

                    if (object instanceof ServerJoinGamePacket packet) {

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
                        sendPacket(new ClientPluginMessagePacket("minecraft:brand", writeString(Unpooled.buffer(), "vanilla")));

                        if (!connected && bot.user().getAccount().getSettings().displayBotInfo) {
                            bot.user().sendMessage(bot.user().getLanguage().remoteBotInfoBotSwitchedServerMessageFormat, bot.name());
                            connected = true;
                        }

                    }

                    else if (object instanceof ServerDisconnectPacket packet)
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

        } catch (@NotNull Exception ignored) {}

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
