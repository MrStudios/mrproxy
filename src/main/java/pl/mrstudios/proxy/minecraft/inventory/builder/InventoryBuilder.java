package pl.mrstudios.proxy.minecraft.inventory.builder;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.inventory.ItemStack;
import pl.mrstudios.proxy.netty.connection.Connection;

import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static pl.mrstudios.proxy.util.ProtocolUtil.openWindowPlayerPacket;
import static pl.mrstudios.proxy.util.ProtocolUtil.setWindowItemPacket;

public class InventoryBuilder {

    private int id;
    private int type;
    private Component title;
    private Map<Integer, ItemStack> items = new HashMap<>();

    public @NotNull InventoryBuilder id(int id) {
        this.id = id;
        return this;
    }

    public @NotNull InventoryBuilder type(int type) {
        this.type = type;
        return this;
    }

    public @NotNull InventoryBuilder title(@NotNull String title) {
        this.title = miniMessage().deserialize(title);
        return this;
    }

    public @NotNull InventoryBuilder fill(@NotNull ItemBuilder itemBuilder) {
        for (int i = 0; i < ((this.type + 1) * 9); i++)
            this.items.put(i, itemBuilder.build());
        return this;
    }

    public @NotNull InventoryBuilder item(
            @NotNull Integer slot,
            @NotNull ItemBuilder item
    ) {
        this.items.put(slot, item.build());
        return this;
    }

    public void display(@NotNull Connection connection) {

        openWindowPlayerPacket(connection, this.id, this.type, this.title);
        this.items.forEach(
                (key, value) -> setWindowItemPacket(connection, this.id, 1, key.shortValue(), value)
        );

    }

    public static @NotNull InventoryBuilder inventoryBuilder() {
        return new InventoryBuilder();
    }

}
