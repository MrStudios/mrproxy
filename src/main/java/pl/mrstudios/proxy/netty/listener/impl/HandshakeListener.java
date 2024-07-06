package pl.mrstudios.proxy.netty.listener.impl;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.netty.codec.NettyPacketCodec;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.enums.ConnectionState;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.listener.PacketListener;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.impl.handshake.HandshakePacket;

public class HandshakeListener implements PacketListener<HandshakePacket> {

    @Inject
    public HandshakeListener() {}

    @Override
    public void handle(@NotNull Connection connection, @NotNull HandshakePacket packet) {

        NettyPacketCodec packetCodec = connection.getChannel().pipeline().get(NettyPacketCodec.class);

        connection.setProtocol(packet.getProtocolVersion());
        packetCodec.setConnectionState(ConnectionState.getById(packet.getNextState()));
        packetCodec.setMinecraftVersion(MinecraftVersion.getById(packet.getProtocolVersion()));

    }

    @Override
    public @NotNull Class<? extends Packet> listeningPacket() {
        return HandshakePacket.class;
    }

}
