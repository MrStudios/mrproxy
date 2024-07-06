package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.service.impl.authorization.AuthorizationService;
import pl.mrstudios.proxy.core.user.User;

import static com.google.common.hash.Hashing.sha384;
import static java.nio.charset.StandardCharsets.UTF_8;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(name = "register")
public class CommandRegister {

    private final AuthorizationService authorizationService;

    @Inject
    public CommandRegister(
            @NotNull AuthorizationService authorizationService
    ) {
        this.authorizationService = authorizationService;
    }

    @Execute
    public void execute(
            @Context @HasGroup(USER) User user,
            @Arg("password") String password,
            @Arg("repeated password") String repeatedPassword
    ) {

        if (this.authorizationService.isLogged(user)) {
            user.sendMessage(user.getLanguage().errorCommandNotFound);
            return;
        }

        if (!user.getAccount().getData().password.isBlank()) {
            user.sendMessage(user.getLanguage().commandRegisterAlreadyRegistered);
            return;
        }

        if (!password.equals(repeatedPassword)) {
            user.sendMessage(user.getLanguage().commandRegisterPasswordsNotMatch);
            return;
        }

        this.authorizationService.login(user);
        user.sendMessage(user.getLanguage().commandRegisterSuccess);
        user.getAccount().getData().password = sha384()
                .hashString(password, UTF_8)
                .toString();

    }

}
