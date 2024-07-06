package pl.mrstudios.proxy.netty.listener;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.packet.Packet;

public interface PacketListener<PACKET> {
    void handle(@NotNull Connection connection, @NotNull PACKET packet);
    @NotNull Class<? extends Packet> listeningPacket();
}
