package pl.mrstudios.proxy.core.command.platform.command;

import dev.rollczi.litecommands.argument.parser.input.ParseableInput;
import dev.rollczi.litecommands.command.CommandRoute;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.platform.PlatformInvocationListener;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.command.Command;
import pl.mrstudios.proxy.core.command.platform.sender.ProxySender;
import pl.mrstudios.proxy.core.user.User;

import static dev.rollczi.litecommands.argument.parser.input.ParseableInput.raw;

public class ProxyCommand implements Command {

    private final CommandRoute<User> commandSection;
    private final PlatformInvocationListener<User> executeListener;

    public ProxyCommand(
            @NotNull CommandRoute<User> command,
            @NotNull PlatformInvocationListener<User> executeListener
    ) {
        this.commandSection = command;
        this.executeListener = executeListener;
    }

    @Override
    public void execute(
            @NotNull User user,
            @NotNull String command,
            @NotNull String[] args
    ) {

        ParseableInput<?> raw = raw(args);
        this.executeListener.execute(
                new Invocation<>(user, new ProxySender(user), this.commandSection.getName(), command, raw), raw
        );

    }

}
