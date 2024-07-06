package pl.mrstudios.proxy.core.listener;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.connection.impl.RemoteServerConnection;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.InventoryClickEvent;
import pl.mrstudios.proxy.event.impl.ProxyPacketReceivedEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;
import pl.mrstudios.proxy.minecraft.entity.Location;
import pl.mrstudios.proxy.minecraft.inventory.ItemStack;
import pl.mrstudios.proxy.netty.codec.NettyPacketCodec;
import pl.mrstudios.proxy.netty.packet.impl.play.client.*;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerPlayerPositionAndLookPacket;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.PLAY;
import static pl.mrstudios.proxy.util.ProtocolUtil.setWindowItemPacket;

public class ProxyUserPacketListener implements Listener {

    private final UserManager userManager;
    private final EventManager eventManager;

    @Inject
    public ProxyUserPacketListener(
            @NotNull UserManager userManager,
            @NotNull EventManager eventManager
    ) {
        this.userManager = userManager;
        this.eventManager = eventManager;
    }

    @EventHandler
    public void onPacketReceived(@NotNull ProxyPacketReceivedEvent event) {

        if (event.connection().getConnectionState() != PLAY)
            return;

        if (
                event.packet() instanceof ClientKeepAlivePacket
        ) return;

        if (event.packet() instanceof ClientChatMessagePacket packet && (packet.getMessage().startsWith("@") || packet.getMessage().startsWith(",")))
            return;

        User user = this.userManager.user(event.connection().getGameProfile().getName());
        if (user == null)
            return;

        RemoteServerConnection connection = (RemoteServerConnection) user.getRemoteConnection();

        if (connection == null)
            return;

        if (connection.channel == null)
            return;

        if (!connection.isConnected() || connection.channel.pipeline().get(NettyPacketCodec.class).getConnectionState() != PLAY)
            return;

        connection.sendPacket(event.packet());
        if (user.isMotherEnabled())
            runAsync(
                    () -> user.getBots().forEach((bot) -> {
                        try {
                            MILLISECONDS.sleep(user.getAccount().getSettings().motherDelay);
                            bot.connection().sendPacket(event.packet());
                        } catch (Exception ignored) {}
                    })
            );

    }

    @EventHandler
    public void onUserMovePacket(@NotNull ProxyPacketReceivedEvent event) {

        if (event.connection().getConnectionState() != PLAY)
            return;

        if (
                !(event.packet() instanceof ClientPlayerPositionPacket) && !(event.packet() instanceof ClientPlayerPositionAndRotationPacket)
        ) return;

        if (
                (event.packet() instanceof ClientPlayerPositionPacket packet ? packet.getLocation() : ((ClientPlayerPositionAndRotationPacket) event.packet()).getLocation()).getY() >= 50
        ) return;

        ofNullable(this.userManager.user(event.connection().getGameProfile().getName()))
                .filter((user) -> user.getRemoteConnection() == null)
                .ifPresent(
                        (user) -> event.connection().sendPacket(new ServerPlayerPositionAndLookPacket(
                                new Location(0.5, 64, 0.5, 0, 0),
                                (byte) 0x00, 1, false
                        ))
                );

    }

    @EventHandler
    public void onInventoryClickPacket(@NotNull ProxyPacketReceivedEvent event) {

        if (!(event.packet() instanceof ClientClickWindowSlotPacket packet))
            return;

        if (packet.getWindowId() >= 0 || packet.getHeldItem() == null || packet.getHeldItem().id() == 0)
            return;

        User user = this.userManager.user(event.connection().getGameProfile().getName());
        checkNotNull(user, "user is null");

        InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(user, packet.getWindowId(), (int) packet.getSlot(), packet.getHeldItem());
        this.eventManager.call(inventoryClickEvent);

        if (!inventoryClickEvent.isCancelled())
            return;

        setWindowItemPacket(event.connection(), -1, packet.getStateId(), (short) -1, EMPTY_ITEM);
        setWindowItemPacket(event.connection(), packet.getWindowId(), packet.getStateId(), packet.getSlot(), packet.getHeldItem());

    }

    protected static final ItemStack EMPTY_ITEM = new ItemStack(0, 0, CompoundBinaryTag.builder().build());

}
