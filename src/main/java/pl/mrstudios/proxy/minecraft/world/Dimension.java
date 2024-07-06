package pl.mrstudios.proxy.minecraft.world;

import lombok.Getter;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.Application;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static net.kyori.adventure.nbt.BinaryTagTypes.COMPOUND;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.MINECRAFT_1_19_4;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.MINECRAFT_1_20_1;

@Getter
public enum Dimension {

    OVERWORLD(0, "minecraft:overworld"),
    THE_NETHER(1, "minecraft:the_nether"),
    THE_END(2, "minecraft:the_end");

    private final int id;
    private final String key;

    Dimension(
            @NotNull Integer id,
            @NotNull String key
    ) {
        this.id = id;
        this.key = key;
    }

    public static @NotNull Dimension getById(int id) {
        return stream(values())
                .filter((dimension) -> dimension.id == id)
                .findFirst()
                .orElse(OVERWORLD);
    }

    public static @NotNull CompoundBinaryTag dimensionCodec(@NotNull MinecraftVersion minecraftVersion) {
        return dimensionCodec.computeIfAbsent(minecraftVersion, (key) -> {

            CompoundBinaryTag.Builder codec = CompoundBinaryTag.builder();
            CompoundBinaryTag.Builder dimensionEntry = CompoundBinaryTag.builder();
            ListBinaryTag.Builder<CompoundBinaryTag> dimensionValues = ListBinaryTag.builder(COMPOUND);

            stream(values())
                    .map((dimension) -> CompoundBinaryTag.builder()
                            .putString("name", dimension.key)
                            .putInt("id", dimension.id)
                            .put("element", dimensionData(dimension, minecraftVersion))
                            .build()
                    )
                    .forEach(dimensionValues::add);

            dimensionEntry.putString("type", "minecraft:dimension_type");
            dimensionEntry.put("value", dimensionValues.build());

            codec.put("minecraft:dimension_type", dimensionEntry.build());
            codec.put(
                    "minecraft:worldgen/biome", CompoundBinaryTag.builder()
                            .putString("type", "minecraft:worldgen/biome")
                            .put("value", ListBinaryTag.from(
                                    stream(Biome.values())
                                            .map(Biome::encode)
                                            .toList())
                            )
                            .build()
            );

            if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4)) {
                codec.put("minecraft:chat_type", chatType());
                codec.put("minecraft:damage_type",
                        minecraftVersion.isNewerOrEqual(MINECRAFT_1_20_1) ?
                                damageType120() : damageType1194()
                );
            }

            return codec.build();

        });
    }

    public static @NotNull CompoundBinaryTag dimensionData(@NotNull Dimension dimension, @NotNull MinecraftVersion minecraftVersion) {

        return CompoundBinaryTag.builder()
                .putBoolean("piglin_safe", false)
                .putBoolean("has_raids", false)
                .putInt("monster_spawn_light_level", 0)
                .putInt("monster_spawn_block_light_limit", 0)
                .putBoolean("natural", false)
                .putFloat("ambient_light", 0.0f)
                .putString("infiniburn", minecraftVersion.isNewerOrEqual(MinecraftVersion.MINECRAFT_1_18_2) ? "#" : "")
                .putBoolean("respawn_anchor_works", false)
                .putBoolean("has_skylight", true)
                .putBoolean("bed_works", false)
                .putString("effects", dimension.key)
                .putInt("min_y", 0)
                .putInt("height", 256)
                .putInt("logical_height", 256)
                .putFloat("coordinate_scale", 1.0f)
                .putBoolean("ultrawarm", false)
                .putBoolean("has_ceiling", false)
                .build();

    }

    private static final Map<MinecraftVersion, CompoundBinaryTag> dimensionCodec = new EnumMap<>(MinecraftVersion.class);

    private static @NotNull CompoundBinaryTag chatType() {

        try (InputStream inputStream = Application.class.getClassLoader().getResourceAsStream("mapping/chat_type.nbt")) {
            assert inputStream != null;
            return BinaryTagIO.unlimitedReader().read(inputStream, BinaryTagIO.Compression.GZIP);
        } catch (Exception ignored) {}

        throw new UnsupportedOperationException("Exception Occurred");

    }

    private static @NotNull CompoundBinaryTag damageType1194() {

        try (InputStream inputStream = Application.class.getClassLoader().getResourceAsStream("mapping/damage_type.nbt")) {
            assert inputStream != null;
            return BinaryTagIO.unlimitedReader().read(inputStream, BinaryTagIO.Compression.GZIP);
        } catch (Exception ignored) {}

        throw new UnsupportedOperationException("Exception Occurred");

    }

    private static @NotNull CompoundBinaryTag damageType120() {

        try (InputStream inputStream = Application.class.getClassLoader().getResourceAsStream("mapping/damage_type_1_20.nbt")) {
            assert inputStream != null;
            return BinaryTagIO.unlimitedReader().read(inputStream, BinaryTagIO.Compression.GZIP);
        } catch (Exception ignored) {}

        throw new UnsupportedOperationException("Exception Occurred");

    }

}
