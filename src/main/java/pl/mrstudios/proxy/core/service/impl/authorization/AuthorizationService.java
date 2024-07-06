package pl.mrstudios.proxy.core.service.impl.authorization;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.service.Service;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.ProxyUserAuthorizedEvent;
import pl.mrstudios.proxy.event.impl.ProxyUserDisconnectedEvent;
import pl.mrstudios.proxy.event.impl.ProxyUserJoinedEvent;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerSetExperiencePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.join;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofMillis;
import static java.util.Arrays.stream;
import static java.util.List.of;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.Times.times;
import static net.kyori.adventure.title.Title.title;
import static pl.mrstudios.proxy.util.StringUtil.plural;

public class AuthorizationService implements Service {

    private final MiniMessage miniMessage;
    private final UserManager userManager;
    private final EventManager eventManager;
    private final Map<String, Integer> users;

    @Inject
    public AuthorizationService(
            @NotNull MiniMessage miniMessage,
            @NotNull UserManager userManager,
            @NotNull EventManager eventManager
    ) {
        this.users = new HashMap<>();
        this.miniMessage = miniMessage;
        this.userManager = userManager;
        this.eventManager = eventManager;
    }

    @Override
    public void run() {
        this.users.keySet()
                .stream().filter((name) -> this.users.get(name) >= 0)
                .map(this.userManager::user).filter(Objects::nonNull)
                .forEach((user) -> {

                    int time = this.users.get(user.getName());

                    if (time <= 0) {
                        user.disconnect(
                                this.miniMessage.deserialize(join("<br>", of(
                                        "<reset>",
                                        "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>",
                                        "<dark_gray>Proxy Disconnected</dark_gray>",
                                        "<reset>",
                                        "<dark_aqua>Time for authorization elapsed.</dark_aqua>",
                                        "<reset>"
                                )))
                        );
                        return;
                    }

                    if (stream(messageTimes).anyMatch((messageTime) -> messageTime == time))
                        user.sendMessage(
                                (user.getAccount().getData().password.isBlank()) ?
                                        user.getLanguage().serviceAuthorizationMustRegisterMessage : user.getLanguage().serviceAuthorizationMustLoginMessage
                        );

                    setExperience(user, time, time / 30.0f);
                    user.sendTitle(title(
                            this.miniMessage.deserialize(user.getLanguage().serviceAuthorizationTitle),
                            this.miniMessage.deserialize(String.format(
                                    user.getLanguage().serviceAuthorizationSubtitle,
                                    time, plural((long) time, user.getLanguage().pluralOneSecond, user.getLanguage().pluralTwoSeconds, user.getLanguage().pluralFiveSeconds)
                            )), times(ZERO, ofMillis(2500), ZERO)
                    ));

                    this.users.put(user.getName(), time - 1);

                });
    }

    @EventHandler
    public void onUserJoin(@NotNull ProxyUserJoinedEvent event) {
        this.users.put(event.user().getName(), 30);
    }

    @EventHandler
    public void onUserQuit(@NotNull ProxyUserDisconnectedEvent event) {
        this.users.remove(event.user().getName());
    }

    public boolean isLogged(@NotNull User user) {
        return this.users.getOrDefault(user.getName(), -1) < 0;
    }

    public void login(@NotNull User user) {
        setExperience(user, 0, 0.0f);
        user.sendTitle(title(empty(), empty()));
        this.users.put(user.getName(), -1);
        this.eventManager.call(new ProxyUserAuthorizedEvent(user));
    }

    protected static void setExperience(@NotNull User user, int level, float progress) {
        user.getConnection().sendPacket(new ServerSetExperiencePacket(progress, level, 0));
    }

    protected static final int[] messageTimes = new int[] { 5, 10, 15, 20, 25, 30 };

}
