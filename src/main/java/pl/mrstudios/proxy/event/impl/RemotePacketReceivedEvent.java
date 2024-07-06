package pl.mrstudios.proxy.event.impl;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.connection.RemoteConnection;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.event.Event;
import pl.mrstudios.proxy.netty.packet.Packet;

public record RemotePacketReceivedEvent(
        @NotNull User user,
        @NotNull Packet packet,
        @NotNull RemoteConnection connection
) implements Event {}
