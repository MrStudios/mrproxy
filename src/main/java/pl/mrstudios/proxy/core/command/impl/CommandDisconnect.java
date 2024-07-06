package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerBotConnection;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerGhostConnection;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;

import java.util.Objects;

import static java.util.Optional.ofNullable;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(
        name = "disconnect",
        aliases = {
                "quit",
                "q"
        }
)
@CommandDescription(
        name = "disconnect",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Disconnect from remote server."),
                @LanguageEntry(key = POLISH, value = "Odłączenie od zdalnego serwera."),
                @LanguageEntry(key = RUSSIAN, value = "Отключиться от удаленного сервера.")
        },
        parameters = {
                @Parameter(
                        name = "me/bots/ghosts",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Select disconnect target."),
                                @LanguageEntry(key = POLISH, value = "Wybierz cel rozłączenia."),
                                @LanguageEntry(key = RUSSIAN, value = "Выберите цель отключения.")
                        }
                )
        }
)
public class CommandDisconnect {

    @Inject
    public CommandDisconnect() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        ofNullable(user.getRemoteConnection()).ifPresentOrElse(
                (connection) -> connection.disconnect(null, true),
                () -> user.sendMessage(user.getLanguage().errorYouMustBeConnected)
        );

    }

    @Execute(name = "me")
    public void executeMe(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {
        this.execute(user);
    }

    @Execute(name = "bots")
    public void executeBots(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        if (user.getRemoteConnection() == null) {
            user.sendMessage(user.getLanguage().errorYouMustBeConnected);
            return;
        }

        if (user.getBots().isEmpty()) {
            user.sendMessage(user.getLanguage().errorNoConnectedBots);
            return;
        }

        user.sendMessage(user.getLanguage().commandDisconnectDisconnectedBots);
        user.getBots()
                .stream()
                .filter(Objects::nonNull).filter((bot) -> bot.connection().isConnected())
                .filter((bot) -> bot.connection() instanceof RemoteServerBotConnection)
                .forEach((bot) -> bot.connection().disconnect());
        user.getBots().removeIf((bot) -> bot.connection() instanceof RemoteServerBotConnection);

    }

    @Execute(name = "ghosts")
    public void executeGhosts(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        if (user.getRemoteConnection() == null) {
            user.sendMessage(user.getLanguage().errorYouMustBeConnected);
            return;
        }

        if (user.getBots().isEmpty()) {
            user.sendMessage(user.getLanguage().errorNoConnectedBots);
            return;
        }

        user.sendMessage(user.getLanguage().commandDisconnectDisconnectedGhost);
        user.getBots()
                .stream()
                .filter(Objects::nonNull).filter((bot) -> bot.connection().isConnected())
                .filter((bot) -> bot.connection() instanceof RemoteServerGhostConnection)
                .forEach((bot) -> bot.connection().disconnect());
        user.getBots().removeIf((bot) -> bot.connection() instanceof RemoteServerGhostConnection);

    }

}
