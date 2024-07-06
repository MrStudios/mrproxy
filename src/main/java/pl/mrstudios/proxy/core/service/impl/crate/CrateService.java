package pl.mrstudios.proxy.core.service.impl.crate;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.config.Configuration;
import pl.mrstudios.proxy.core.service.Service;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.InventoryClickEvent;
import pl.mrstudios.proxy.event.impl.ProxyPacketReceivedEvent;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientBlockPlacementPacket;

import java.time.Duration;

import static java.time.Duration.ZERO;
import static java.util.Optional.ofNullable;
import static pl.mrstudios.proxy.minecraft.inventory.builder.InventoryBuilder.inventoryBuilder;
import static pl.mrstudios.proxy.minecraft.inventory.builder.ItemBuilder.itemBuilder;
import static pl.mrstudios.proxy.util.MappingUtil.blackGlassItemIdFor;

public class CrateService implements Service {

    private final UserManager userManager;
    private final Configuration configuration;

    @Inject
    public CrateService(
            @NotNull UserManager userManager,
            @NotNull Configuration configuration
    ) {
        this.userManager = userManager;
        this.configuration = configuration;
    }

    @Override
    public void run() {}

    @Override
    public Duration repeatDelay() {
        return ZERO;
    }

    @EventHandler
    public void onBlockClick(@NotNull ProxyPacketReceivedEvent event) {

        if (!(event.packet() instanceof ClientBlockPlacementPacket packet))
            return;

        if (
                packet.getBlockLocation().getX() != this.configuration.general.crateLocation.getX() ||
                packet.getBlockLocation().getY() != this.configuration.general.crateLocation.getY() ||
                packet.getBlockLocation().getZ() != this.configuration.general.crateLocation.getZ()
        ) return;

        ofNullable(this.userManager.user(event.connection().getGameProfile().getName()))
                .ifPresent(
                        (user) -> inventoryBuilder()
                                .id(-21).type(3)
                                .fill(
                                        itemBuilder(blackGlassItemIdFor(user.getConnection().getMinecraftVersion()), 1)
                                                .name("<reset>").itemFlags((byte) 0x80)
                                )
                                .title("<reset>          <dark_aqua><b>ᴍʏᴛʜɪᴄ ᴄʀᴀᴛᴇ")
                                .display(event.connection())
                );

    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {

        if (event.getWindowId() != -21)
            return;

        event.setCancelled(true);

    }

}
