package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Pair;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerConnection;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;

import static java.lang.String.format;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static panda.std.Pair.of;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(name = "list")
@CommandDescription(
        name = "list",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Display connected users to proxy."),
                @LanguageEntry(key = POLISH, value = "Wyświetl połączonych użytkowników."),
                @LanguageEntry(key = RUSSIAN, value = "Отобразить подключенных пользователей.")
        }
)
public class CommandList {

    private final UserManager userManager;

    @Inject
    public CommandList(
            @NotNull UserManager userManager
    ) {
        this.userManager = userManager;
    }

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        user.sendMessage(
                user.getLanguage().commandList, this.userManager.users().size(),
                this.userManager.users()
                        .stream()
                        .sorted(comparingInt((object) -> object.getAccount().getGroup().getPermissionLevel() * -1))
                        .map((entry) -> {

                            Pair<String, String> userData = userEntry(entry);

                            return format(
                                    user.getLanguage().commandListEntry, format(
                                            user.getLanguage().commandListEntryHover,
                                            userData == null ? user.getLanguage().wordDisconnected
                                                    : userData.getFirst(),
                                            userData == null ? user.getLanguage().wordDisconnected
                                                    : userData.getSecond()
                                    ), entry.getConnection().getMinecraftVersion().getName(), entry.getAccount().getGroup().getPrefix(), entry.getName()
                            );

                        }).collect(joining())
        );

    }

    protected static @Nullable Pair<String, String> userEntry(@NotNull User user) {

        if (user.getRemoteConnection() == null)
            return null;

        RemoteServerConnection connection = (RemoteServerConnection) user.getRemoteConnection();

        return user.getAccount().getSettings().incognito ? of(
                format("<obfuscated>%s</obfuscated>", random(connection.name.length())),
                format("<obfuscated>%s</obfuscated>", random(connection.credentials.toString().length()))
        ) : of(connection.name, connection.credentials.toString());

    }

}
