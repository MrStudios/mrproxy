package pl.mrstudios.proxy.core.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.ProxyUserAuthorizedEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerChatMessagePacket;

import java.util.UUID;

import static java.lang.String.format;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static pl.mrstudios.proxy.minecraft.component.ChatMessageType.SYSTEM;
import static pl.mrstudios.proxy.util.StringUtil.expiresFormatHighest;

public class ProxyUserAuthorizedListener implements Listener {

    private final MiniMessage miniMessage;

    @Inject
    public ProxyUserAuthorizedListener(
            @NotNull MiniMessage miniMessage
    ) {
        this.miniMessage = miniMessage;
    }

    @EventHandler
    public void onUserAuthorized(@NotNull ProxyUserAuthorizedEvent event) {

        event.user().getConnection().sendPacket(new ServerChatMessagePacket(this.miniMessage.deserialize("<br>".repeat(100)), SYSTEM, EMPTY));
        event.user().sendMessage(
                event.user().getLanguage().welcomeMessage,
                (between(now(), event.user().getAccount().getExpires()).toDays() <= 7) ?
                        format(
                                event.user().getLanguage().welcomeMessageExpireLine, expiresFormatHighest(event.user().getLanguage(), between(now(), event.user().getAccount().getExpires()))
                        ) : ""
        );

    }

    protected static final UUID EMPTY = new UUID(0, 0);

}
