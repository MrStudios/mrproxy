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

@Command(name = "incognito")
@CommandDescription(
        name = "incognito",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Toggle displaying your server to other users."),
                @LanguageEntry(key = POLISH, value = "Przełącz wyświetlanie swojego serwera innym użytkownikom."),
                @LanguageEntry(key = RUSSIAN, value = "Переключить отображение вашего сервера другим пользователям.")
        }
)
public class CommandIncognito {

    @Inject
    public CommandIncognito() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        user.getAccount().getSettings().incognito = !user.getAccount().getSettings().incognito;
        user.sendMessage(
                user.getLanguage().proxyOptionChangedMessageFormat,
                "incognito",
                user.getAccount().getSettings().incognito ?
                        user.getLanguage().wordEnabled : user.getLanguage().wordDisabled
        );

    }

}
