package pl.mrstudios.proxy.minecraft.world;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum Difficulty {

    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3);

    private final int id;

    public static Difficulty getById(int id) {
        return stream(values())
                .filter((difficulty) -> difficulty.id == id)
                .findFirst()
                .orElse(PEACEFUL);
    }

}
