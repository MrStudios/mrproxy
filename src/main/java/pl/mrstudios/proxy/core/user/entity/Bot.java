package pl.mrstudios.proxy.core.user.entity;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.connection.RemoteConnection;
import pl.mrstudios.proxy.core.user.User;

public record Bot(
        @NotNull User user,
        @NotNull String name,
        @NotNull RemoteConnection connection
) {}
