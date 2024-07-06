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

@Command(name = "mother")
@CommandDescription(
        name = "mother",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Toggle controlling connected bots."),
                @LanguageEntry(key = POLISH, value = "Przełącz kontrolowanie połączonych botów."),
                @LanguageEntry(key = RUSSIAN, value = "Переключить управление подключенными ботами.")
        },
        parameters = {
                @Parameter(
                        name = "delay",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Delay between another bot moves."),
                                @LanguageEntry(key = POLISH, value = "Opóźnienie między botami."),
                                @LanguageEntry(key = RUSSIAN, value = "Задержка между ботами.")
                        }
                )
        }
)
public class CommandMother {

    @Inject
    public CommandMother() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("delay") @Range(min = 0, max = 5000) Optional<Integer> delay
    ) {

        delay.ifPresentOrElse(
                (motherDelay) -> {
                    user.getAccount().getSettings().motherDelay = motherDelay;
                    user.sendMessage(
                            user.getLanguage().proxyOptionChangedMessageFormat,
                            "mother delay", motherDelay + "ms"
                    );
                }, () -> {
                    user.setMotherEnabled(!user.isMotherEnabled());
                    user.sendMessage(
                            user.getLanguage().proxyOptionChangedMessageFormat,
                            "mother",
                            user.isMotherEnabled() ?
                                    user.getLanguage().wordEnabled : user.getLanguage().wordDisabled
                    );
                }
        );

    }

}
