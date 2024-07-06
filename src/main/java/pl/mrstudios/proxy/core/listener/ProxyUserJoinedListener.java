package pl.mrstudios.proxy.core.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.ProxyUserJoinedEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;
import pl.mrstudios.proxy.minecraft.component.ChatMessageType;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerChatMessagePacket;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.join;
import static java.util.Arrays.stream;
import static pl.mrstudios.proxy.util.ReflectUtil.defaultValueOf;

public class ProxyUserJoinedListener implements Listener {

    private final MiniMessage miniMessage;
    private final UserManager userManager;

    @Inject
    public ProxyUserJoinedListener(
            @NotNull MiniMessage miniMessage,
            @NotNull UserManager userManager
    ) {
        this.miniMessage = miniMessage;
        this.userManager = userManager;
    }

    @EventHandler
    public void onUserJoin(@NotNull ProxyUserJoinedEvent event) {

        if (profileNeedUpdate(event.user())) {
            event.user().getConnection().disconnect(
                    this.miniMessage.deserialize(join("<br>", List.of(
                            "<reset>",
                            "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>",
                            "<dark_gray>Proxy Disconnected</dark_gray>",
                            "<reset>",
                            "<dark_aqua>Your profile was updated to latest version,</dark_aqua>",
                            "<dark_aqua>please join again. If you lost any data please</dark_aqua>",
                            "<dark_aqua>contact with our support instantly.</dark_aqua>",
                            "<reset>"
                    )))
            );
            return;
        }

        this.userManager.users().forEach(
                (target) -> target.sendMessage(target.getLanguage().proxyJoinMessageFormat, event.user().getConnection().getMinecraftVersion().getName(), event.user().getAccount().getGroup().name(), event.user().getAccount().getName())
        );

        event.user().setConnected(true);
        event.user().lobby(false);
        event.user().getConnection().sendPacket(
                new ServerChatMessagePacket(this.miniMessage.deserialize("<br>".repeat(100)), ChatMessageType.SYSTEM, EMPTY)
        );

    }

    protected static boolean profileNeedUpdate(@NotNull User user) {
        return updateFields(user.getAccount().getSettings())
                || updateFields(user.getAccount().getData());
    }

    protected static boolean updateFields(@NotNull Object object) {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        stream(object.getClass().getFields()).toList().stream()
                .peek((field) -> field.setAccessible(true))
                .forEach((field) -> {

                    try {

                        if (field.get(object) == null) {
                            field.set(object, defaultValueOf(field.getType()));
                            atomicBoolean.set(true);
                        }

                    } catch (Exception ignored) {}

                });

        return atomicBoolean.get();

    }

    protected static final UUID EMPTY = new UUID(0, 0);

}
