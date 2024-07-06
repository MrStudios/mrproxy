package pl.mrstudios.proxy.event.impl;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.event.Event;

public record ProxyUserJoinedEvent(
        @NotNull User user
) implements Event {}
