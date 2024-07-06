package pl.mrstudios.proxy.core.command.handler;

import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.user.User;

public class ValidatorResultHandler implements ResultHandler<User, String> {

    private final MiniMessage miniMessage;

    @Inject
    public ValidatorResultHandler(@NotNull MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public void handle(
            @NotNull Invocation<User> invocation,
            @NotNull String string,
            @NotNull ResultHandlerChain<User> resultHandlerChain
    ) {
        invocation.sender().sendMessage(this.miniMessage.deserialize(string));
    }

}
