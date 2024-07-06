package pl.mrstudios.proxy.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum MinecraftVersion {

    UNKNOWN("Unknown", 0),
    MINECRAFT_1_16_5("1.16.5", 754),
    MINECRAFT_1_18_2("1.18.2", 758),
    MINECRAFT_1_19_4("1.19.4", 762),
    MINECRAFT_1_20_1("1.20.1", 763);

    private final String name;
    private final int protocol;

    public boolean isNewerOrEqual(MinecraftVersion version) {
        return this.protocol >= version.protocol;
    }

    public static MinecraftVersion getById(int id) {
        return stream(values())
                .filter((version) -> version.protocol == id)
                .findFirst()
                .orElse(UNKNOWN);
    }

}
