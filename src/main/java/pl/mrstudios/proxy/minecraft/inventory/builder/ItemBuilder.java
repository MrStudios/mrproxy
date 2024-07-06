package pl.mrstudios.proxy.minecraft.inventory.builder;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.minecraft.inventory.ItemStack;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static net.kyori.adventure.nbt.BinaryTagTypes.STRING;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;
public class ItemBuilder {

    /* Item Required Data */
    private final @NotNull Integer id;
    private final @NotNull Integer amount;

    /* Item Optional Data */
    private @Nullable Component name;
    private @Nullable List<Component> lore;
    private @Nullable Byte itemFlags;

    public ItemBuilder(
            @NotNull Integer id,
            @NotNull Integer amount
    ) {
        this.id = id;
        this.amount = amount;
    }

    public @NotNull ItemBuilder name(@NotNull String name) {
        this.name = miniMessage().deserialize(name).decoration(ITALIC, false);
        return this;
    }

    public @NotNull ItemBuilder lore(@NotNull List<String> lore) {
        this.lore = lore.stream()
                .map(miniMessage()::deserialize)
                .map((component) -> component.decoration(ITALIC, false))
                .toList();
        return this;
    }

    public @NotNull ItemBuilder lore(@NotNull String... lore) {
        return lore(stream(lore).toList());
    }

    public @NotNull ItemBuilder itemFlags(@NotNull Byte itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public @NotNull ItemStack build() {

        CompoundBinaryTag.Builder parentTag = CompoundBinaryTag.builder(),
                displayTag = CompoundBinaryTag.builder();

        ofNullable(this.name)
                .ifPresent((name) -> displayTag.putString("Name", gson().serialize(name)));

        ofNullable(this.lore)
                .ifPresent((lore) -> displayTag.put(
                        "Lore", ListBinaryTag.builder(STRING)
                                .add(lore.stream()
                                        .map(gson()::serialize)
                                        .map(StringBinaryTag::stringBinaryTag)
                                        .toList()
                                ).build()
                ));

        ofNullable(this.itemFlags)
                .ifPresent((itemFlags) -> parentTag.putByte("HideFlags", itemFlags));

        parentTag.put("display", displayTag.build());

        return new ItemStack(
                this.id, this.amount, parentTag.build()
        );

    }

    public static @NotNull ItemBuilder itemBuilder(
            @NotNull Integer id,
            @NotNull Integer amount
    ) {
        return new ItemBuilder(id, amount);
    }

}
