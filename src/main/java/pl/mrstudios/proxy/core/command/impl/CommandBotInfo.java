package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;

import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(name = "botinfo")
@CommandDescription(
        name = "botinfo",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Toggle displaying information about bots."),
                @LanguageEntry(key = POLISH, value = "Przełącz wyświetlanie informacji o botach."),
                @LanguageEntry(key = RUSSIAN, value = "Переключить отображение информации о ботах.")
        }
)
public class CommandBotInfo {

    @Inject
    public CommandBotInfo() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        user.getAccount().getSettings().displayBotInfo = !user.getAccount().getSettings().displayBotInfo;
        user.sendMessage(
                user.getLanguage().proxyOptionChangedMessageFormat,
                "botinfo",
                user.getAccount().getSettings().displayBotInfo ?
                        user.getLanguage().wordEnabled : user.getLanguage().wordDisabled
        );

    }

}
