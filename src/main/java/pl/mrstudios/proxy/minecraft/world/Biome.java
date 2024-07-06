package pl.mrstudios.proxy.minecraft.world;


import lombok.AllArgsConstructor;
import lombok.Builder;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public enum Biome {

    PLAINS(
            1,
            "minecraft:plains",
            new Element(
                    true, 0.125F, 0.8F, 0.05F, 0.4F, "plains",
                    Effects.builder()
                            .skyColor(7907327)
                            .waterFogColor(329011)
                            .fogColor(12638463)
                            .waterColor(415920)
                            .moodSound(
                                    Effects.MoodSound.builder()
                                            .tickDelay(6000)
                                            .offset(2.0)
                                            .blockSearchExtent(8)
                                            .sound("minecraft:ambient.cave")
                                            .build()
                            )
                            .build()
            )
    ),
    SWAMP(
            6,
            "minecraft:swamp",
            new Element(
                    true, -0.2F, 0.8F, 0.1F, 0.9F, "swamp",
                    Effects.builder()
                            .skyColor(7907327)
                            .waterFogColor(329011)
                            .fogColor(12638463)
                            .waterColor(415920)
                            .foliageColor(6975545)
                            .moodSound(
                                    Effects.MoodSound.builder()
                                            .tickDelay(6000)
                                            .offset(2.0)
                                            .blockSearchExtent(8)
                                            .sound("minecraft:ambient.cave")
                                            .build()
                            )
                            .build()
            )
    ),
    SWAMP_HILLS(
            134,
            "minecraft:swamp_hills",
            new Element(
                    true, -0.2F, 0.8F, 0.1F, 0.9F, "swamp",
                    Effects.builder()
                            .skyColor(7907327)
                            .waterFogColor(329011)
                            .fogColor(12638463)
                            .waterColor(415920)
                            .foliageColor(6975545)
                            .moodSound(
                                    Effects.MoodSound.builder()
                                            .tickDelay(6000)
                                            .offset(2.0)
                                            .blockSearchExtent(8)
                                            .sound("minecraft:ambient.cave")
                                            .build()
                            )
                            .build()
            )
    ),
    NETHER_WASTES(
            8,
            "minecraft:nether_wastes",
            new Element(false, 0.1f, 2.0f, 0.2f, 0.0f, "nether",
                    Effects.builder()
                            .skyColor(7254527)
                            .waterFogColor(329011)
                            .fogColor(3344392)
                            .waterColor(4159204)
                            .moodSound(
                                    Effects.MoodSound.builder()
                                            .tickDelay(6000)
                                            .offset(2.0)
                                            .blockSearchExtent(8)
                                            .sound("minecraft:ambient.nether_wastes.mood")
                                            .build()
                            )
                            .build()
            )
    ),
    THE_END(
            9,
            "minecraft:the_end",
            new Element(false, 0.1f, 0.5f, 0.2f, 0.5f, "the_end",
                    Effects.builder()
                            .skyColor(0)
                            .waterFogColor(10518688)
                            .fogColor(12638463)
                            .waterColor(4159204)
                            .moodSound(
                                    Effects.MoodSound.builder()
                                            .tickDelay(6000)
                                            .offset(2.0)
                                            .blockSearchExtent(8)
                                            .sound("minecraft:ambient.cave")
                                            .build()
                            )
                            .build()
            )
    );

    private final int id;
    private final String name;
    private final Element element;

    public CompoundBinaryTag encode() {
        return CompoundBinaryTag.builder()
                .putString("name", this.name)
                .putInt("id", this.id)
                .put("element", this.element.encode())
                .build();
    }

    public record Element(
            @NotNull Boolean precipitation,
            @NotNull Float depth,
            @NotNull Float temperature,
            @NotNull Float scale,
            @NotNull Float downfall,
            @NotNull String category,
            @NotNull Effects effects
    ) {

        public CompoundBinaryTag encode() {
            return CompoundBinaryTag.builder()
                    .putString("precipitation", this.precipitation ? "rain" : "none")
                    .putBoolean("has_precipitation", this.precipitation)
                    .putFloat("depth", this.depth)
                    .putFloat("temperature", this.temperature)
                    .putFloat("scale", this.scale)
                    .putFloat("downfall", this.downfall)
                    .putString("category", this.category)
                    .put("effects", this.effects.encode())
                    .build();
        }

    }

    @Builder(builderClassName = "Builder")
    public record Effects(
            @NotNull Integer skyColor,
            @NotNull Integer waterFogColor,
            @NotNull Integer fogColor,
            @NotNull Integer waterColor,
            @Nullable Integer foliageColor,
            @Nullable String grassColorModifier,
            @Nullable Music music,
            @Nullable String ambientSound,
            @Nullable AdditionsSound additionsSound,
            @Nullable MoodSound moodSound,
            @Nullable Particle particle
    ) {

        public CompoundBinaryTag encode() {

            CompoundBinaryTag.Builder compoundTag = CompoundBinaryTag.builder();

            compoundTag.putInt("sky_color", this.skyColor);
            compoundTag.putInt("water_fog_color", this.waterColor);
            compoundTag.putInt("fog_color", this.fogColor);
            compoundTag.putInt("water_color", this.waterColor);

            if (this.foliageColor != null)
                compoundTag.putInt("foliage_color", this.foliageColor);

            if (this.grassColorModifier != null)
                compoundTag.putString("grass_color_modifier", this.grassColorModifier);

            if (this.music != null)
                compoundTag.put("music", this.music.encode());

            if (this.ambientSound != null)
                compoundTag.putString("ambient_sound", this.ambientSound);

            if (this.additionsSound != null)
                compoundTag.put("additions_sound", this.additionsSound.encode());

            if (this.moodSound != null)
                compoundTag.put("mood_sound", this.moodSound.encode());

            if (this.particle != null)
                compoundTag.put("particle", this.particle.encode());

            return compoundTag.build();

        }

        @lombok.Builder(builderClassName = "Builder")
        public record MoodSound(
                @NotNull Integer tickDelay,
                @NotNull Double offset,
                @NotNull Integer blockSearchExtent,
                @NotNull String sound
        ) {

            public CompoundBinaryTag encode() {
                return CompoundBinaryTag.builder()
                        .putString("sound", this.sound)
                        .putInt("tick_delay", this.tickDelay)
                        .putDouble("offset", this.offset)
                        .putInt("block_search_extent", this.blockSearchExtent)
                        .build();
            }

        }

        @lombok.Builder(builderClassName = "Builder")
        public record Music(
                @NotNull Boolean replaceCurrentMusic,
                @NotNull String sound,
                @NotNull Integer maxDelay,
                @NotNull Integer minDelay
        ) {

            public CompoundBinaryTag encode() {
                return CompoundBinaryTag.builder()
                        .putBoolean("replace_current_music", this.replaceCurrentMusic)
                        .putString("sound", this.sound)
                        .putInt("max_delay", this.maxDelay)
                        .putInt("min_delay", this.minDelay)
                        .build();
            }

        }

        @lombok.Builder(builderClassName = "Builder")
        public record AdditionsSound(
                @NotNull  String sound,
                @NotNull Double tickChance
        ) {

            public CompoundBinaryTag encode() {
                return CompoundBinaryTag.builder()
                        .putString("sound", this.sound)
                        .putDouble("tick_chance", this.tickChance)
                        .build();
            }

        }

        @lombok.Builder(builderClassName = "Builder")
        public record Particle(
                @NotNull Float probability,
                @NotNull ParticleOptions options
        ) {

            public CompoundBinaryTag encode() {
                return CompoundBinaryTag.builder()
                        .putFloat("probability", this.probability)
                        .put("options", this.options.encode())
                        .build();
            }

            public record ParticleOptions(
                    @NotNull String type
            ) {

                public CompoundBinaryTag encode() {
                    return CompoundBinaryTag.builder()
                            .putString("type", this.type)
                            .build();
                }

            }

        }

    }

}
