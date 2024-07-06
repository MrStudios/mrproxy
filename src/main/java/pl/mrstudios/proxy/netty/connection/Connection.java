package pl.mrstudios.proxy.netty.connection;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.profile.GameProfile;
import pl.mrstudios.proxy.netty.codec.NettyCompressionCodec;
import pl.mrstudios.proxy.netty.codec.NettyPacketCodec;
import pl.mrstudios.proxy.netty.enums.ConnectionState;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginDisconnectPacket;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginSetCompressionPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerDisconnectPacket;

import java.net.SocketAddress;

import static java.util.Optional.ofNullable;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.LOGIN;

@Getter @Setter
public class Connection {

    private Channel channel;
    private SocketAddress address;

    /* Minecraft Stuff */
    private int protocol;
    private GameProfile gameProfile;

    public Connection(@NotNull Channel channel) {
        this.channel = channel;
    }


    public void disconnect(@NotNull Component reason) {
        this.sendPacket(
                (this.getConnectionState() == LOGIN) ?
                        new ServerLoginDisconnectPacket(reason) : new ServerDisconnectPacket(reason)
        );
        this.channel.close();
    }

    public void setCompressionThreshold(int threshold) {

        if (this.getConnectionState() != LOGIN)
            return;

        this.sendPacket(new ServerLoginSetCompressionPacket(threshold));
        ofNullable(this.channel.pipeline().get(NettyCompressionCodec.class))
                .orElseGet(
                        () -> this.channel.pipeline()
                                .addBefore("packet-codec", "compression-codec", new NettyCompressionCodec(threshold))
                                .get(NettyCompressionCodec.class)
                ).setCompressionThreshold(threshold);
    }

    public @NotNull SocketAddress getAddress() {
        return ofNullable(this.address)
                .orElse(this.channel.remoteAddress());
    }

    public void sendPacket(@NotNull Packet packet) {
        this.channel.writeAndFlush(packet, this.channel.voidPromise());
    }

    public @NotNull MinecraftVersion getMinecraftVersion() {
        return this.channel.pipeline().get(NettyPacketCodec.class).getMinecraftVersion();
    }

    public @NotNull ConnectionState getConnectionState() {
        return this.channel.pipeline().get(NettyPacketCodec.class).getConnectionState();
    }

    public void setConnectionState(@NotNull ConnectionState connectionState) {
        this.channel.pipeline().get(NettyPacketCodec.class).setConnectionState(connectionState);
    }

}
