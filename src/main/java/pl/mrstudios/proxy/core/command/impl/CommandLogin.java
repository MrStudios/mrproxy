package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.service.impl.authorization.AuthorizationService;
import pl.mrstudios.proxy.core.user.User;

import java.util.List;

import static com.google.common.hash.Hashing.sha384;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(name = "login")
public class CommandLogin {

    private final MiniMessage miniMessage;
    private final AuthorizationService authorizationService;

    @Inject
    public CommandLogin(
            @NotNull MiniMessage miniMessage,
            @NotNull AuthorizationService authorizationService
    ) {
        this.miniMessage = miniMessage;
        this.authorizationService = authorizationService;
    }

    @Execute
    public void execute(
            @Context @HasGroup(USER) User user,
            @Arg("password") String password
    ) {

        if (this.authorizationService.isLogged(user)) {
            user.sendMessage(user.getLanguage().errorCommandNotFound);
            return;
        }

        if (user.getAccount().getData().password.isBlank()) {
            user.sendMessage(user.getLanguage().commandLoginNotRegistered);
            return;
        }

        if (!user.getAccount().getData().password.equals(sha384().hashString(password, UTF_8).toString())) {
            user.disconnect(
                    this.miniMessage.deserialize(join("<br>", List.of(
                            "<reset>",
                            "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>",
                            "<dark_gray>Proxy Disconnected</dark_gray>",
                            "<reset>",
                            "<dark_aqua>Provided password is invalid.</dark_aqua>",
                            "<reset>"
                    )))
            );
            return;
        }

        this.authorizationService.login(user);
        user.sendMessage(user.getLanguage().commandLoginSuccess);

    }

}
