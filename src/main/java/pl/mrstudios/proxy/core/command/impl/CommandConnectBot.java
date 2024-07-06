package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import panda.std.Pair;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.command.platform.annotations.Range;
import pl.mrstudios.proxy.core.connection.ConnectionCredentials;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerBotConnection;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerConnection;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.service.impl.proxy.ProxyService;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.core.user.enums.Group;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;
import static pl.mrstudios.proxy.util.StringGenerationUtil.generateString;

@Command(
        name = "connectbot",
        aliases = { "joinbot" }
)
@CommandDescription(
        name = "connectbot",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Connect bots to remote server."),
                @LanguageEntry(key = POLISH, value = "Połącz boty do zdalnego serwera."),
                @LanguageEntry(key = RUSSIAN, value = "Подключить ботов к удаленному серверу.")
        },
        parameters = {
                @Parameter(
                        name = "amount",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Amount of bots to connect."),
                                @LanguageEntry(key = POLISH, value = "Ilość botów do połączenia."),
                                @LanguageEntry(key = RUSSIAN, value = "Количество ботов для подключения.")
                        }
                ),
                @Parameter(
                        name = "delay",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Delay between bot connections."),
                                @LanguageEntry(key = POLISH, value = "Opóźnienie między połączeniami botów."),
                                @LanguageEntry(key = RUSSIAN, value = "Задержка между подключениями ботов.")
                        }
                ),
                @Parameter(
                        name = "proxy",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Type of proxy address."),
                                @LanguageEntry(key = POLISH, value = "Typ adresu proxy."),
                                @LanguageEntry(key = RUSSIAN, value = "Тип адреса прокси.")
                        }
                ),
                @Parameter(
                        name = "name",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Nickname which will be used to connect."),
                                @LanguageEntry(key = POLISH, value = "Nazwa użytkownika która zostanie użyta do połączenia."),
                                @LanguageEntry(key = RUSSIAN, value = "Никнейм, который будет использоваться для подключения.")
                        }
                )
        }
)
public class CommandConnectBot {

    private final UserManager userManager;
    private final EventManager eventManager;
    private final ProxyService proxyService;
    private final PacketRegistry packetRegistry;

    @Inject
    public CommandConnectBot(
            @NotNull UserManager userManager,
            @NotNull ProxyService proxyService,
            @NotNull EventManager eventManager,
            @NotNull PacketRegistry packetRegistry
    ) {
        this.userManager = userManager;
        this.proxyService = proxyService;
        this.eventManager = eventManager;
        this.packetRegistry = packetRegistry;
    }

    @Execute
    @SneakyThrows
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("amount") @Range(min = 1) int amount,
            @Arg("delay") @Range(min = 100, max = 5000) int delay,
            @Arg("proxy") String proxy,
            @Arg("name") String name
    ) {

        if (user.getRemoteConnection() == null) {
            user.sendMessage(user.getLanguage().errorYouMustBeConnected);
            return;
        }

        if ((user.getBots().size() + amount) > user.getAccount().getGroup().getMaxBots()) {
            user.sendMessage(user.getLanguage().commandConnectBotTooManyBots);
            return;
        }

        String remoteName = ((RemoteServerConnection) user.getRemoteConnection()).name;
        ConnectionCredentials connectionCredentials = ((RemoteServerConnection) user.getRemoteConnection()).credentials;

        this.userManager.users()
                .forEach((target) -> target.sendMessage(
                        target.getLanguage().proxyRemoteConnectingBotsMessageFormat, user.getAccount().getName(),
                        user.getAccount().getSettings().incognito ? String.format("<obfuscated>%s</obfuscated>", RandomStringUtils.random(remoteName.length())) : remoteName, amount,
                        user.getAccount().getSettings().incognito ? String.format("<obfuscated>%s</obfuscated>", RandomStringUtils.random(connectionCredentials.toString().length())) : connectionCredentials.toString()
                ));

        for (int i = 0; i < amount; i++) {

            if (user.getRemoteConnection() == null)
                return;

            Pair<Group, ProxyEntry> proxyEntry = this.proxyService.getProxy(proxy);
            if (proxyEntry == null) {
                user.sendMessage(user.getLanguage().errorProxyListEmpty, proxy.toLowerCase());
                return;
            }

            if (proxyEntry.getFirst().getPermissionLevel() > user.getAccount().getGroup().getPermissionLevel()) {
                user.sendMessage(user.getLanguage().errorProxyNoAccessToType, proxy.split(":")[0].toUpperCase());
                return;
            }

            int integer = i;
            runAsync(
                    () -> new RemoteServerBotConnection(
                            user, generateString(name, 16, integer), proxyEntry.getSecond(), this.eventManager, this.packetRegistry, ((RemoteServerConnection) user.getRemoteConnection()).credentials
                    )
            );
            MILLISECONDS.sleep(delay);

        }

    }

}
