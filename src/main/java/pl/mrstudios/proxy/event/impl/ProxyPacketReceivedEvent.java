package pl.mrstudios.proxy.event.impl;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.event.Event;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.packet.Packet;

public record ProxyPacketReceivedEvent(
        @NotNull Connection connection,
        @NotNull Packet packet
) implements Event {}
