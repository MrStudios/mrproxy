package pl.mrstudios.proxy.util;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;

public class MappingUtil {

    public static int bookItemIdFor(@NotNull MinecraftVersion minecraftVersion) {
        return switch (minecraftVersion) {

            case MINECRAFT_1_16_5 -> 678;
            case MINECRAFT_1_18_2 -> 792;
            case MINECRAFT_1_19_4 -> 881;
            case MINECRAFT_1_20_1 -> 885;

            default -> 0;

        };
    }

    public static int blackGlassItemIdFor(@NotNull MinecraftVersion minecraftVersion) {
        return switch (minecraftVersion) {

            case MINECRAFT_1_16_5 -> 410;
            case MINECRAFT_1_18_2 -> 431;
            case MINECRAFT_1_19_4 -> 478;
            case MINECRAFT_1_20_1 -> 480;

            default -> 0;

        };
    }

    public static int mineCartItemIdFor(@NotNull MinecraftVersion minecraftVersion) {
        return switch (minecraftVersion) {

            case MINECRAFT_1_16_5 -> 663;
            case MINECRAFT_1_18_2 -> 662;
            case MINECRAFT_1_19_4 -> 724;
            case MINECRAFT_1_20_1 -> 728;

            default -> 0;

        };
    }

    public static int mineCartChestItemIdFor(@NotNull MinecraftVersion minecraftVersion) {
        return switch (minecraftVersion) {

            case MINECRAFT_1_16_5 -> 680;
            case MINECRAFT_1_18_2 -> 663;
            case MINECRAFT_1_19_4 -> 725;
            case MINECRAFT_1_20_1 -> 729;

            default -> 0;

        };
    }

    public static int mineCartTNTItemIdFor(@NotNull MinecraftVersion minecraftVersion) {
        return switch (minecraftVersion) {

            case MINECRAFT_1_16_5 -> 851;
            case MINECRAFT_1_18_2 -> 665;
            case MINECRAFT_1_19_4 -> 727;
            case MINECRAFT_1_20_1 -> 731;

            default -> 0;

        };
    }

}
