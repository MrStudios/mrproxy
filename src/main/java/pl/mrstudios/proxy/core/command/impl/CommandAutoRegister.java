package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.RemoteBotPacketReceivedEvent;
import pl.mrstudios.proxy.event.impl.RemotePacketReceivedEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientChatMessagePacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerChatMessagePacket;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.BitSet.valueOf;
import static java.util.regex.Pattern.compile;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;
import static pl.mrstudios.proxy.minecraft.component.ChatMessageType.ACTIONBAR;
import static pl.mrstudios.proxy.util.StringUtil.removeLegacyColors;

@Command(name = "autoregister")
@CommandDescription(
        name = "autoregister",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Configure automatic registration."),
                @LanguageEntry(key = POLISH, value = "Ustawienia automatycznej rejestracji."),
                @LanguageEntry(key = RUSSIAN, value = "Настройки автоматической регистрации.")
        },
        parameters = {
                @Parameter(
                        name = "player/bots",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Who changes will affect."),
                                @LanguageEntry(key = POLISH, value = "Kogo zmiany będą dotyczyć."),
                                @LanguageEntry(key = RUSSIAN, value = "Кого изменения будут касаться.")
                        }
                ),
                @Parameter(
                        name = "password",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Set automatic registration password."),
                                @LanguageEntry(key = POLISH, value = "Ustaw hasło do automatycznej rejestracji."),
                                @LanguageEntry(key = RUSSIAN, value = "Установить пароль для автоматической регистрации.")
                        }
                )
        }
)
public class CommandAutoRegister implements Listener {

    @Inject
    public CommandAutoRegister() {}

    @Execute
    public void executeNoArguments(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {
        user.sendMessage(user.getLanguage().errorInvalidCommandUsage, ",autoregister <player/bots> <password>");
    }

    @Execute(name = "player")
    public void executePlayer(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("password") Optional<String> optional
    ) {
        optional.ifPresentOrElse(
                (password) -> {
                    user.getAccount().getSettings().autoRegisterPlayerPassword = password;
                    user.sendMessage(user.getLanguage().commandAutoRegisterPasswordSet, "PLAYER", password);
                },
                () -> {
                    user.getAccount().getSettings().autoRegisterPlayer = !user.getAccount().getSettings().autoRegisterPlayer;
                    user.sendMessage(
                            user.getLanguage().proxyOptionChangedMessageFormat,
                            "autoregister player",
                            user.getAccount().getSettings().autoRegisterPlayer ?
                                    user.getLanguage().wordEnabled : user.getLanguage().wordDisabled
                    );
                }
        );
    }

    @Execute(name = "bots")
    public void executeBots(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("password") Optional<String> optional
    ) {
        optional.ifPresentOrElse(
                (password) -> {
                    user.getAccount().getSettings().autoRegisterBotsPassword = password;
                    user.sendMessage(user.getLanguage().commandAutoRegisterPasswordSet, "BOTS", password);
                },
                () -> {
                    user.getAccount().getSettings().autoRegisterBots = !user.getAccount().getSettings().autoRegisterBots;
                    user.sendMessage(
                            user.getLanguage().proxyOptionChangedMessageFormat,
                            "autoregister bots",
                            user.getAccount().getSettings().autoRegisterBots ?
                                    user.getLanguage().wordEnabled : user.getLanguage().wordDisabled
                    );
                }
        );
    }

    @EventHandler /* Automatic Login */
    public void automaticLoginListener(@NotNull RemotePacketReceivedEvent event) {

        if (event.user().getRemoteConnection() == null)
            return;

        if (!(event.packet() instanceof ServerChatMessagePacket packet))
            return;

        if (packet.getType() == ACTIONBAR)
            return;

        if (!event.user().getAccount().getSettings().autoRegisterPlayer)
            return;

        if (!LOGIN_COMMAND_PATTERN.matcher(removeLegacyColors(plainText().serialize(packet.getMessage()))).find())
            return;

        event.user().sendMessage(event.user().getLanguage().serviceAutoRegisterAutomaticallyLoggedMessage, event.user().getAccount().getSettings().autoRegisterPlayerPassword);
        event.user().getRemoteConnection().sendPacket(new ClientChatMessagePacket(format(
                "/login %s", event.user().getAccount().getSettings().autoRegisterPlayerPassword
        ), now().toEpochMilli(), 0, null, 0, valueOf(new long[0])));

    }

    @EventHandler /* Automatic Registration */
    public void automaticRegistrationListener(@NotNull RemotePacketReceivedEvent event) {

        if (event.user().getRemoteConnection() == null)
            return;

        if (!(event.packet() instanceof ServerChatMessagePacket packet))
            return;

        if (packet.getType() == ACTIONBAR)
            return;

        if (!event.user().getAccount().getSettings().autoRegisterPlayer)
            return;

        if (!REGISTER_COMMAND_PATTERN.matcher(removeLegacyColors(plainText().serialize(packet.getMessage()))).find())
            return;

        event.user().sendMessage(event.user().getLanguage().serviceAutoRegisterAutomaticallyRegisteredMessage, event.user().getAccount().getSettings().autoRegisterPlayerPassword);
        event.user().getRemoteConnection().sendPacket(new ClientChatMessagePacket(format(
                "/register %s %s", event.user().getAccount().getSettings().autoRegisterPlayerPassword, event.user().getAccount().getSettings().autoRegisterPlayerPassword
        ), now().toEpochMilli(), 0, null, 0, valueOf(new long[0])));

    }

    @EventHandler /* Automatic Login */
    public void automaticBotLoginListener(@NotNull RemoteBotPacketReceivedEvent event) {

        if (!(event.packet() instanceof ServerChatMessagePacket packet))
            return;

        if (packet.getType() == ACTIONBAR)
            return;

        if (!event.bot().user().getAccount().getSettings().autoRegisterBots)
            return;

        if (!LOGIN_COMMAND_PATTERN.matcher(removeLegacyColors(plainText().serialize(packet.getMessage()))).find())
            return;

        event.bot().connection().sendPacket(new ClientChatMessagePacket(format(
                "/login %s", event.bot().user().getAccount().getSettings().autoRegisterBotsPassword
        ), now().toEpochMilli(), 0, null, 0, valueOf(new long[0])));

    }

    @EventHandler /* Automatic Registration */
    public void automaticBotRegistrationListener(@NotNull RemoteBotPacketReceivedEvent event) {

        if (!(event.packet() instanceof ServerChatMessagePacket packet))
            return;

        if (packet.getType() == ACTIONBAR)
            return;

        if (!event.bot().user().getAccount().getSettings().autoRegisterBots)
            return;

        if (!REGISTER_COMMAND_PATTERN.matcher(removeLegacyColors(plainText().serialize(packet.getMessage()))).find())
            return;

        event.bot().connection().sendPacket(new ClientChatMessagePacket(format(
                "/register %s %s", event.bot().user().getAccount().getSettings().autoRegisterBotsPassword, event.bot().user().getAccount().getSettings().autoRegisterBotsPassword
        ), now().toEpochMilli(), 0, null, 0, valueOf(new long[0])));

    }

    protected static final Pattern LOGIN_COMMAND_PATTERN = compile("/login|/l|/zaloguj");
    protected static final Pattern REGISTER_COMMAND_PATTERN = compile("/register|/reg|/zarejestruj|/konto");

}
