package pl.mrstudios.proxy.event.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.event.Event;
import pl.mrstudios.proxy.event.interfaces.Cancellable;
import pl.mrstudios.proxy.minecraft.inventory.ItemStack;

@Getter @Setter
public class InventoryClickEvent implements Event, Cancellable {

    private final User user;
    private final Integer windowId;
    private final Integer clickedSlot;
    private final ItemStack clickedItem;

    private boolean cancelled;

    public InventoryClickEvent(
            @NotNull User user,
            @NotNull Integer windowId,
            @NotNull Integer clickedSlot,
            @Nullable ItemStack clickedItem
    ) {
        this.user = user;
        this.windowId = windowId;
        this.clickedSlot = clickedSlot;
        this.clickedItem = clickedItem;
        this.cancelled = false;
    }

}
