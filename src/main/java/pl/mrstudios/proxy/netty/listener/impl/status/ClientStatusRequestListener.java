package pl.mrstudios.proxy.netty.listener.impl.status;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.config.Configuration;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.minecraft.profile.GameProfile;
import pl.mrstudios.proxy.minecraft.status.PlayerInfo;
import pl.mrstudios.proxy.minecraft.status.ServerInfo;
import pl.mrstudios.proxy.minecraft.status.VersionInfo;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.listener.PacketListener;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.impl.status.client.ClientStatusRequestPacket;
import pl.mrstudios.proxy.netty.packet.impl.status.server.ServerStatusResponsePacket;
import pl.mrstudios.proxy.util.StringUtil;

import java.util.UUID;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.nio.file.Path.of;
import static pl.mrstudios.proxy.core.Application.VERSION;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.MINECRAFT_1_16_5;
import static pl.mrstudios.proxy.util.StreamUtil.saveResource;
import static pl.mrstudios.proxy.util.StringUtil.applyLegacyColors;
import static pl.mrstudios.proxy.util.StringUtil.readImage;

@SuppressWarnings("all")
public class ClientStatusRequestListener implements PacketListener<ClientStatusRequestPacket> {

    private final UserManager userManager;
    private final MiniMessage miniMessage;
    private final Configuration configuration;

    protected final PlayerInfo playerInfo;
    protected final VersionInfo versionInfo;
    protected final ServerInfo legacy, modern;

    @Inject
    public ClientStatusRequestListener(
            @NotNull UserManager userManager,
            @NotNull MiniMessage miniMessage,
            @NotNull Configuration configuration
    ) {

        /* Initialize */
        this.userManager = userManager;
        this.miniMessage = miniMessage;
        this.configuration = configuration;

        /* Initialize Fields */
        this.playerInfo = new PlayerInfo(
                16_08_2006,
                0,
                this.configuration.display.hover.stream()
                        .map(StringUtil::applyLegacyColors)
                        .map((string) -> new GameProfile(UUID.randomUUID(), string))
                        .toList()
        );

        this.versionInfo = new VersionInfo(
                applyLegacyColors(format(this.configuration.display.version, VERSION, 0)), 1
        );

        /* Create Server Info */
        this.legacy = new ServerInfo(
                this.miniMessage.deserialize(join("<br>", this.configuration.display.descriptionLegacy)),
                readImage("./assets/logo.png"), this.playerInfo, this.versionInfo
        );

        this.modern = this.legacy.duplicate();
        this.modern.setDescription(this.miniMessage.deserialize(join("<br>", this.configuration.display.descriptionModern)));

    }

    @Override
    public void handle(@NotNull Connection connection, @NotNull ClientStatusRequestPacket packet) {
        this.playerInfo.setOnline(this.userManager.users().size());
        this.versionInfo.setName(applyLegacyColors(format(this.configuration.display.version, VERSION, this.userManager.users().size())));
        connection.getChannel().writeAndFlush(new ServerStatusResponsePacket(
                (connection.getProtocol() >= MINECRAFT_1_16_5.getProtocol()) ? this.modern : this.legacy
        ));
    }

    @Override
    public @NotNull Class<? extends Packet> listeningPacket() {
        return ClientStatusRequestPacket.class;
    }

    {
        saveResource(of("./assets/logo.png"), "assets/logo.png");
    }

}
