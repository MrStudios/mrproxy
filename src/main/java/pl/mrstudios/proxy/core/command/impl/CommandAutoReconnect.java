package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.command.platform.annotations.Range;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;

import java.util.Optional;

import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(name = "autoreconnect")
@CommandDescription(
        name = "autoreconnect",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Toggle automatic reconnection."),
                @LanguageEntry(key = POLISH, value = "Przełącz automatyczne ponowne łączenie."),
                @LanguageEntry(key = RUSSIAN, value = "Переключить автоматическое повторное подключение.")
        },
        parameters = {
                @Parameter(
                        name = "delay",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Delay between connections."),
                                @LanguageEntry(key = POLISH, value = "Opóźnienie między połączeniami."),
                                @LanguageEntry(key = RUSSIAN, value = "Задержка между подключениями.")
                        }
                )
        }
)
public class CommandAutoReconnect {

    @Inject
    public CommandAutoReconnect() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("delay") @Range(min = 0, max = 15000) Optional<Integer> delay
    ) {

        delay.ifPresentOrElse(
                (reconnectDelay) -> {
                    user.getAccount().getSettings().autoReconnectPlayerDelay = reconnectDelay;
                    user.sendMessage(
                            user.getLanguage().proxyOptionChangedMessageFormat,
                            "autoreconnect delay", reconnectDelay + "ms"
                    );
                }, () -> {
                    user.getAccount().getSettings().autoReconnectPlayer = !user.getAccount().getSettings().autoReconnectPlayer;
                    user.sendMessage(
                            user.getLanguage().proxyOptionChangedMessageFormat,
                            "autoreconnect",
                            user.getAccount().getSettings().autoReconnectPlayer ?
                                    user.getLanguage().wordEnabled : user.getLanguage().wordDisabled
                    );
                }
        );

    }

}
