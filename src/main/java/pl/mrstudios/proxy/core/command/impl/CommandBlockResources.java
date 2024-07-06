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

@Command(
        name = "blockresources",
        aliases = {
                "blockresourcepack",
                "blockresourcepacks",
                "blockresource"
        }
)
@CommandDescription(
        name = "blockresources",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Toggle blocking server resourcepakcs."),
                @LanguageEntry(key = POLISH, value = "Przełącz blokowanie paczek zasobów serwera."),
                @LanguageEntry(key = RUSSIAN, value = "Переключить блокировку ресурсов сервера."),
        }
)
public class CommandBlockResources {

    @Inject
    public CommandBlockResources() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        user.getAccount().getSettings().blockResourcePacks = !user.getAccount().getSettings().blockResourcePacks;
        user.sendMessage(
                user.getLanguage().proxyOptionChangedMessageFormat,
                "block resourcepacks",
                user.getAccount().getSettings().blockResourcePacks ?
                        user.getLanguage().wordEnabled : user.getLanguage().wordDisabled
        );

    }

}
