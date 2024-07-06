package pl.mrstudios.proxy.netty.listener.impl.status;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.listener.PacketListener;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.impl.status.client.ClientStatusPingPacket;
import pl.mrstudios.proxy.netty.packet.impl.status.server.ServerStatusPongPacket;

import static java.lang.System.currentTimeMillis;

public class ClientStatusPingListener implements PacketListener<ClientStatusPingPacket> {

    @Inject
    public ClientStatusPingListener() {}

    @Override
    public void handle(@NotNull Connection connection, @NotNull ClientStatusPingPacket packet) {
        connection.getChannel().writeAndFlush(new ServerStatusPongPacket(currentTimeMillis() - packet.getPing()));
        connection.getChannel().close();
    }

    @Override
    public @NotNull Class<? extends Packet> listeningPacket() {
        return ClientStatusPingPacket.class;
    }

}
