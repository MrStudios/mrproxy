package pl.mrstudios.proxy.core.listener;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandManager;
import pl.mrstudios.proxy.core.service.impl.authorization.AuthorizationService;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.ProxyPacketReceivedEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;
import pl.mrstudios.proxy.logger.Logger;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientChatMessagePacket;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.copyOfRange;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.regex.Pattern.compile;

public class ProxyChatMessagePacketListener implements Listener {

    private final Logger logger;
    private final UserManager userManager;
    private final CommandManager commandManager;
    private final AuthorizationService authorizationService;

    @Inject
    public ProxyChatMessagePacketListener(
            @NotNull Logger logger,
            @NotNull UserManager userManager,
            @NotNull CommandManager commandManager,
            @NotNull AuthorizationService authorizationService
    ) {
        this.logger = logger;
        this.userManager = userManager;
        this.commandManager = commandManager;
        this.authorizationService = authorizationService;
    }

    @EventHandler
    public void onCommandMessage(@NotNull ProxyPacketReceivedEvent event) {

        if (!(event.packet() instanceof ClientChatMessagePacket packet))
            return;

        if (!packet.getMessage().startsWith(","))
            return;

        User user = this.userManager.user(event.connection().getGameProfile().getName());
        String[] args = packet.getMessage().substring(1).split(" ");

        checkNotNull(user, "user is null");

        if (!this.authorizationService.isLogged(user) && !args[0].equalsIgnoreCase("login") && !args[0].equalsIgnoreCase("register")) {
            user.sendMessage(user.getLanguage().errorMustBeLogged);
            return;
        }

        this.commandManager.getCommands()
                .keySet().stream()
                .filter((command) -> command.equalsIgnoreCase(args[0]))
                .findFirst()
                .ifPresentOrElse(
                        (command) -> {
                            this.logger.info("User %s executed command %s.", user.getAccount().getName(), packet.getMessage());
                            newSingleThreadExecutor().submit(() -> this.commandManager.getCommands().get(command).execute(user, args[0], copyOfRange(args, 1, args.length)));
                        }, () -> user.sendMessage(user.getLanguage().errorCommandNotFound)
                );

    }

    @EventHandler
    public void onChatMessage(@NotNull ProxyPacketReceivedEvent event) {

        if (!(event.packet() instanceof ClientChatMessagePacket packet))
            return;

        if (!packet.getMessage().startsWith("@"))
            return;

        User user = this.userManager.user(event.connection().getGameProfile().getName());
        checkNotNull(user, "user is null");

        if (!this.authorizationService.isLogged(user)) {
            user.sendMessage(user.getLanguage().errorMustBeLogged);
            return;
        }

        if (packet.getMessage().substring(1).isBlank()) {
            user.sendMessage(user.getLanguage().errorMustEnterMessage);
            return;
        }

        this.logger.info("(CHAT) %s: %s", user.getAccount().getName(), packet.getMessage().substring(1));
        this.userManager.users()
                .forEach((target) -> target.sendMessage(
                        target.getLanguage().proxyChatMessageFormat,
                        user.getConnection().getMinecraftVersion().getName(),
                        user.getAccount().getGroup().getPrefix(),
                        user.getAccount().getName(),
                        filter(packet.getMessage().substring(1))
                ));

    }

    protected static @NotNull String filter(@NotNull String message) {
        return pattern.matcher(message).replaceAll("")
                .replace("\\", "");
    }

    protected static final Pattern pattern = compile("<([a-zA-Z/#]+)(?![^>]*/>)[^>]*>");

}
