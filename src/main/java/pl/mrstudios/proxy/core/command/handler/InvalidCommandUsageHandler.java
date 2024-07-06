package pl.mrstudios.proxy.core.command.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.user.User;

public class InvalidCommandUsageHandler implements InvalidUsageHandler<User> {

    @Inject
    public InvalidCommandUsageHandler() {}

    @Override
    public void handle(
            @NotNull Invocation<User> invocation,
            @NotNull InvalidUsage<User> usage,
            @NotNull ResultHandlerChain<User> result
    ) {
        invocation.sender().sendMessage(
                invocation.sender().getLanguage().errorInvalidCommandUsage, usage.getSchematic().first().replace("/", ",")
        );
    }
}
