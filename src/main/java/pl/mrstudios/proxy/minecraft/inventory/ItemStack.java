package pl.mrstudios.proxy.minecraft.inventory;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;

public record ItemStack(
        int id,
        @NotNull Integer amount,
        @NotNull CompoundBinaryTag nbt
) {}
