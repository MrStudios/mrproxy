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

@Command(name = "lastpacket")
@CommandDescription(
        name = "lastpacket",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Toggle displaying last received packet."),
                @LanguageEntry(key = POLISH, value = "Przełącz wyświetlanie ostatniego odebranego pakietu."),
                @LanguageEntry(key = RUSSIAN, value = "Переключить отображение последнего полученного пакета.")
        }
)
public class CommandLastPacket {

    @Inject
    public CommandLastPacket() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        user.getAccount().getSettings().displayLastPacketReceived = !user.getAccount().getSettings().displayLastPacketReceived;
        user.sendMessage(
                user.getLanguage().proxyOptionChangedMessageFormat,
                "lastpacket",
                user.getAccount().getSettings().displayLastPacketReceived ?
                        user.getLanguage().wordEnabled : user.getLanguage().wordDisabled
        );

    }

}
