package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;

import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(
        name = "chatclear",
        aliases = {
                "cc"
        }
)
@CommandDescription(
        name = "chatclear",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Clear messages on your chat."),
                @LanguageEntry(key = POLISH, value = "Wyczyszczenie wiadomości na czacie."),
                @LanguageEntry(key = RUSSIAN, value = "Очистить сообщения в чате.")
        }
)
public class CommandChatClear {

    @Inject
    public CommandChatClear() {}

    @Execute
    public void execute(@Context @HasGroup(USER) User user) {
        user.sendMessage("<br>".repeat(200));
        user.sendMessage(user.getLanguage().commandChatClearCleared);
    }

}
