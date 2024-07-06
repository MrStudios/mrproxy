package pl.mrstudios.proxy.core.service.impl.notify;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerConnection;
import pl.mrstudios.proxy.core.service.Service;
import pl.mrstudios.proxy.core.service.impl.authorization.AuthorizationService;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.impl.UndefinedPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerPlayerListHeaderAndFooterPacket;

import java.time.Duration;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.Duration.between;
import static java.time.Duration.ofMillis;
import static java.time.Instant.now;
import static java.util.Date.from;
import static pl.mrstudios.proxy.util.StringUtil.expiresFormatHighest;

public class NotifyService implements Service {

    private final MiniMessage miniMessage;
    private final UserManager userManager;
    private final AuthorizationService authorizationService;

    @Inject
    public NotifyService(
            @NotNull MiniMessage miniMessage,
            @NotNull UserManager userManager,
            @NotNull AuthorizationService authorizationService
    ) {
        this.miniMessage = miniMessage;
        this.userManager = userManager;
        this.authorizationService = authorizationService;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {

        try {

            this.userManager.users()
                    .stream()
                    .filter(this.authorizationService::isLogged)
                    .forEach((user) -> {

                        user.getConnection().sendPacket(new ServerPlayerListHeaderAndFooterPacket(
                                this.miniMessage.deserialize(format(
                                        user.getLanguage().tabListHeader,
                                        user.getAccount().getGroup().name(),
                                        (from(user.getAccount().getExpires()).getYear() >= 128) ? "LIFETIME" : expiresFormatHighest(user.getLanguage(), between(now(), user.getAccount().getExpires())),
                                        user.getBots().size(), user.getAccount().getGroup().getMaxBots()
                                )),
                                this.miniMessage.deserialize(format(
                                        user.getLanguage().tabListFooter,
                                        user.getRemoteConnection() != null ?
                                                formatLastPacket(((RemoteServerConnection) user.getRemoteConnection()).lastReceivedPacket, "%s", "%s <dark_gray>[<gray>Id: <aqua>%d</aqua></gray>]</dark_gray>")
                                                :
                                                user.getLanguage().wordDisconnected,
                                        user.getRemoteConnection() != null ?
                                                currentTimeMillis() - ((RemoteServerConnection) user.getRemoteConnection()).lastReceivedPacketTime : 0,
                                        user.getRemoteConnection() != null ?
                                                ((RemoteServerConnection) user.getRemoteConnection()).credentials.toString() : user.getLanguage().wordDisconnected,
                                        user.getRemoteConnection() != null ?
                                                ((RemoteServerConnection) user.getRemoteConnection()).name : user.getLanguage().wordDisconnected
                                ))
                        ));

                        if (user.getRemoteConnection() == null || !(user.getRemoteConnection() instanceof RemoteServerConnection connection))
                            return;

                        if (user.getAccount().getSettings().displayLastPacketReceived)
                            user.sendActionBar(
                                    user.getLanguage().remoteLastPacketReceivedNotifyFormat,
                                    formatLastPacket(connection.lastReceivedPacket, "%s", "%s <dark_gray>[<gray>Id: <dark_aqua>%d</dark_aqua></gray>]</dark_gray>"),
                                    currentTimeMillis() - connection.lastReceivedPacketTime
                            );

                        if ((currentTimeMillis() - connection.lastReceivedPacketTime) > 3000)
                            user.sendTitle(
                                    user.getLanguage().lagDetectionTitle,
                                    format(user.getLanguage().lagDetectionSubtitle, currentTimeMillis() - connection.lastReceivedPacketTime),
                                    0, 1000, 0
                            );

                    });

        } catch (Exception ignored) {}

    }

    @Override
    public Duration repeatDelay() {
        return ofMillis(50);
    }

    protected static @NotNull String formatLastPacket(@NotNull Packet packet, @NotNull String schemeNormal, @NotNull String schemeUndefined) {
        return packet instanceof UndefinedPacket undefinedPacket ?
                format(schemeUndefined, packet.getClass().getSimpleName(), undefinedPacket.getId()) : format(schemeNormal, packet.getClass().getSimpleName());
    }

}
