package pl.mrstudios.proxy.minecraft.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum GameMode {

    UNKNOWN(-1),
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    private final int id;

    public static GameMode getById(int id) {
        return stream(values())
                .filter((gameMode) -> gameMode.id == id)
                .findFirst()
                .orElse(SURVIVAL);
    }

}
