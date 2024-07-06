package pl.mrstudios.proxy.netty.listener.impl.login;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.impl.ProxyLoginEvent;
import pl.mrstudios.proxy.logger.Logger;
import pl.mrstudios.proxy.minecraft.profile.GameProfile;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.listener.PacketListener;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.impl.login.client.ClientLoginStartPacket;

import static java.lang.String.join;
import static java.util.List.of;
import static java.util.UUID.randomUUID;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.UNKNOWN;

public class ClientLoginStartListener implements PacketListener<ClientLoginStartPacket> {

    private final Logger logger;
    private final MiniMessage miniMessage;
    private final EventManager eventManager;

    @Inject
    public ClientLoginStartListener(Logger logger, MiniMessage miniMessage, EventManager eventManager) {
        this.logger = logger;
        this.miniMessage = miniMessage;
        this.eventManager = eventManager;
    }

    @Override
    public void handle(@NotNull Connection connection, @NotNull ClientLoginStartPacket packet) {

        connection.setGameProfile(new GameProfile(randomUUID(), packet.getName()));
        this.logger.info("%s (%s) [%s] has connected.", connection.getGameProfile().getName(), connection.getChannel().remoteAddress().toString(), connection.getMinecraftVersion().getName());

        /* Handle Packet */
        if (connection.getMinecraftVersion() == UNKNOWN) {
            disconnect(connection, "Your client version is not supported by proxy.");
            return;
        }

        ProxyLoginEvent event = new ProxyLoginEvent(connection);

        this.eventManager.call(event);
        if (!event.isCancelled())
            return;

        disconnect(connection, event.getReason());

    }

    @Override
    public @NotNull Class<? extends Packet> listeningPacket() {
        return ClientLoginStartPacket.class;
    }

    protected void disconnect(@NotNull Connection connection, @NotNull String reason) {
        this.logger.info("%s (%s) [%s] was kicked from proxy for '%s'", connection.getGameProfile().getName(), connection.getChannel().remoteAddress().toString(), connection.getMinecraftVersion().getName(), reason);
        connection.disconnect(
                this.miniMessage.deserialize(join("<br>", of(
                        "<reset>",
                        "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>",
                        "<dark_gray>Proxy Disconnected</dark_gray>",
                        "<reset>",
                        String.format("<dark_aqua>%s</dark_aqua>", reason),
                        "<reset>"
                )))
        );
    }

}
