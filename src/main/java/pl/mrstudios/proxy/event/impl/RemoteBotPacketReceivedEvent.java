package pl.mrstudios.proxy.event.impl;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.user.entity.Bot;
import pl.mrstudios.proxy.event.Event;
import pl.mrstudios.proxy.netty.packet.Packet;

public record RemoteBotPacketReceivedEvent(
        @NotNull Bot bot,
        @NotNull Packet packet
) implements Event {}
