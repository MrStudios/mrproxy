package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import panda.std.Pair;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.connection.ConnectionCredentials;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerStatusResolver;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.service.impl.proxy.ProxyService;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.enums.Group;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(
        name = "status",
        aliases = { "resolve" }
)
@CommandDescription(
        name = "status",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Check server status."),
                @LanguageEntry(key = POLISH, value = "Sprawdź status serwera."),
                @LanguageEntry(key = RUSSIAN, value = "Проверить статус сервера.")
        },
        parameters = {
                @Parameter(
                        name = "proxy",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Type of proxy address."),
                                @LanguageEntry(key = POLISH, value = "Typ adresu proxy."),
                                @LanguageEntry(key = RUSSIAN, value = "Тип адреса прокси.")
                        }
                ),
                @Parameter(
                        name = "host",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Server address which will be checked."),
                                @LanguageEntry(key = POLISH, value = "Adres serwera który zostanie sprawdzony."),
                                @LanguageEntry(key = RUSSIAN, value = "Адрес сервера, который будет проверен.")
                        }
                ),
                @Parameter(
                        name = "port",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Server port which will be checked."),
                                @LanguageEntry(key = POLISH, value = "Port serwera który zostanie sprawdzony."),
                                @LanguageEntry(key = RUSSIAN, value = "Порт сервера, который будет проверен.")
                        }
                )
        }
)
public class CommandStatus {

    private final ProxyService proxyService;
    private final PacketRegistry packetRegistry;

    @Inject
    public CommandStatus(
            @NotNull ProxyService proxyService,
            @NotNull PacketRegistry packetRegistry
    ) {
        this.proxyService = proxyService;
        this.packetRegistry = packetRegistry;
    }

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("proxy") String proxy,
            @Arg("host") String host,
            @Arg("port") Optional<Integer> port
    ) {

        user.sendMessage(user.getLanguage().commandStatusChecking);

        Pair<Group, ProxyEntry> proxyEntry = this.proxyService.getProxy(proxy);
        ConnectionCredentials connectionCredentials = new ConnectionCredentials(host, port.orElse(null)).get();

        ofNullable(proxyEntry)
                .ifPresentOrElse(
                        (entry) -> {

                            if (entry.getFirst().getPermissionLevel() > user.getAccount().getGroup().getPermissionLevel()) {
                                user.sendMessage(user.getLanguage().errorProxyNoAccessToType, proxy.split(":")[0].toUpperCase());
                                return;
                            }

                            user.sendMessage(user.getLanguage().commandStatusResolvedHost, connectionCredentials.host(), connectionCredentials.port());
                            new RemoteServerStatusResolver(
                                    user,
                                    connectionCredentials.get(),
                                    entry.getSecond(),
                                    this.packetRegistry
                            );

                        },
                        () -> user.sendMessage(user.getLanguage().errorProxyListEmpty, proxy.toLowerCase())
                );

    }

}
