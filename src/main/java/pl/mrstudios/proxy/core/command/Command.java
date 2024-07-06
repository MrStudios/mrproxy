package pl.mrstudios.proxy.core.command;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.user.User;

public interface Command {
    void execute(@NotNull User user, @NotNull String command, @NotNull String[] args);
}
