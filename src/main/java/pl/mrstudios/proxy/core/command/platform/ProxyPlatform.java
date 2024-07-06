package pl.mrstudios.proxy.core.command.platform;

import dev.rollczi.litecommands.command.CommandRoute;
import dev.rollczi.litecommands.platform.AbstractPlatform;
import dev.rollczi.litecommands.platform.PlatformInvocationListener;
import dev.rollczi.litecommands.platform.PlatformSettings;
import dev.rollczi.litecommands.platform.PlatformSuggestionListener;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.command.Command;
import pl.mrstudios.proxy.core.command.CommandManager;
import pl.mrstudios.proxy.core.command.platform.command.ProxyCommand;
import pl.mrstudios.proxy.core.user.User;

public class ProxyPlatform extends AbstractPlatform<User, PlatformSettings> {

    private final CommandManager commandManager;

    public ProxyPlatform(@NotNull CommandManager commandManager) {
        super(new ProxyPlatformSettings());
        this.commandManager = commandManager;
    }

    @Override
    public void hook(
            @NotNull CommandRoute<User> commandRoute,
            @NotNull PlatformInvocationListener<User> platformInvocationListener,
            @NotNull PlatformSuggestionListener<User> platformSuggestionListener
    ) {
        Command command = new ProxyCommand(commandRoute, platformInvocationListener);
        commandRoute.names().forEach((name) -> this.commandManager.register(name, command));
    }

    @Override
    public void unhook(@NotNull CommandRoute<User> commandRoute) {
        commandRoute.names().forEach(this.commandManager::unregister);
    }

}
