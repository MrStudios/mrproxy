package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerConnection;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerGhostConnection;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.event.EventManager;

import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(name = "detach")
@CommandDescription(
        name = "detach",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Save current session on remote server."),
                @LanguageEntry(key = POLISH, value = "Zostaw aktualną sesję na serwerze."),
                @LanguageEntry(key = RUSSIAN, value = "Сохранить текущую сессию на удаленном сервере.")
        }
)
public class CommandDetach {

    private final EventManager eventManager;

    @Inject
    public CommandDetach(
            @NotNull EventManager eventManager
    ) {
        this.eventManager = eventManager;
    }

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        if (user.getRemoteConnection() == null) {
            user.sendMessage(user.getLanguage().errorYouMustBeConnected);
            return;
        }

        if ((user.getBots().size() + 1) > user.getAccount().getGroup().getMaxBots()) {
            user.sendMessage(user.getLanguage().commandConnectBotTooManyBots);
            return;
        }

        RemoteServerConnection connection = (RemoteServerConnection) user.getRemoteConnection();
        user.setRemoteConnection(null);
        new RemoteServerGhostConnection(this.eventManager, connection);
        user.sendMessage(user.getLanguage().commandDetachDetached);
        user.lobby(true);

    }

}
