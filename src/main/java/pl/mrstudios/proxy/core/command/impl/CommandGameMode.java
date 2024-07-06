package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.minecraft.player.GameMode;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerGameEventPacket;

import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(
        name = "gamemode",
        aliases = { "gm" }
)
@CommandDescription(
        name = "gamemode",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Change your gamemode on server."),
                @LanguageEntry(key = POLISH, value = "Zmień swój tryb gry na serwerze."),
                @LanguageEntry(key = RUSSIAN, value = "Изменить свой режим игры на сервере.")
        },
        parameters = {
                @Parameter(
                        name = "mode",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Gamemode to change."),
                                @LanguageEntry(key = POLISH, value = "Tryb gry do zmiany."),
                                @LanguageEntry(key = RUSSIAN, value = "Режим игры для изменения.")
                        }
                )
        }
)
public class CommandGameMode {

    @Inject
    public CommandGameMode() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("mode") GameMode gameMode
    ) {

        if (user.getRemoteConnection() == null) {
            user.sendMessage(user.getLanguage().errorYouMustBeConnected);
            return;
        }

        user.sendMessage(user.getLanguage().commandGameModeChanged, gameMode.name());
        user.getConnection().sendPacket(new ServerGameEventPacket(3, gameMode.getId()));

    }

}
