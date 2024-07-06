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
import pl.mrstudios.proxy.core.command.platform.annotations.Range;
import pl.mrstudios.proxy.core.connection.ConnectionCredentials;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerConnection;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.service.impl.proxy.ProxyService;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.core.user.enums.Group;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;

import static java.util.Optional.ofNullable;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;
import static pl.mrstudios.proxy.util.ReflectUtil.callVoid;
import static pl.mrstudios.proxy.util.StringGenerationUtil.generateString;

@Command(
        name = "connect",
        aliases = { "join" }
)
@CommandDescription(
        name = "connect",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Connect to remote server."),
                @LanguageEntry(key = POLISH, value = "Połącz się ze zdalnym serwerem."),
                @LanguageEntry(key = RUSSIAN, value = "Подключиться к удаленному серверу.")
        },
        parameters = {
                @Parameter(
                        name = "host",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Server address to connect."),
                                @LanguageEntry(key = POLISH, value = "Adres serwera do połączenia."),
                                @LanguageEntry(key = RUSSIAN, value = "Адрес сервера для подключения.")
                        }
                ),
                @Parameter(
                        name = "port",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Server port to connect."),
                                @LanguageEntry(key = POLISH, value = "Port serwera do połączenia."),
                                @LanguageEntry(key = RUSSIAN, value = "Порт сервера для подключения.")
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
                                @LanguageEntry(key = POLISH, value = "Nazwa gracza która zostanie użyta do połączenia."),
                                @LanguageEntry(key = RUSSIAN, value = "Никнейм, который будет использоваться для подключения.")
                        }
                )
        }
)
public class CommandConnect {

    private final UserManager userManager;
    private final EventManager eventManager;
    private final ProxyService proxyService;
    private final PacketRegistry packetRegistry;

    @Inject
    public CommandConnect(
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
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("host") String host,
            @Arg("port") @Range(min = 0, max = 65535) Integer port,
            @Arg("proxy") String proxy,
            @Arg("name") String name
    ) {

        Pair<Group, ProxyEntry> proxyEntry = this.proxyService.getProxy(proxy);
        ConnectionCredentials connectionCredentials = new ConnectionCredentials(host, port).get();

        ofNullable(proxyEntry)
                .ifPresentOrElse(
                        (entry) -> {

                            if (entry.getFirst().getPermissionLevel() > user.getAccount().getGroup().getPermissionLevel()) {
                                user.sendMessage(user.getLanguage().errorProxyNoAccessToType, proxy.split(":")[0].toUpperCase());
                                return;
                            }

                            ofNullable(user.getRemoteConnection())
                                    .ifPresent((connection) -> connection.disconnect(null, true));

                            while (user.getRemoteConnection() != null)
                                callVoid();

                            user.sendMessage(
                                    user.getLanguage().commandConnectConnecting,
                                    String.format("%s:%d", connectionCredentials.host(), connectionCredentials.port())
                            );

                            user.setRemoteConnection(new RemoteServerConnection(
                                    user, generateString(name, 16, null), entry.getSecond(), this.userManager,
                                    this.eventManager, this.packetRegistry, connectionCredentials
                            ));

                        },
                        () -> user.sendMessage(user.getLanguage().errorProxyListEmpty, proxy.toLowerCase())
                );

    }

}
