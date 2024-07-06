package pl.mrstudios.proxy.minecraft.component;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum TitleAction {

    TITLE(0),
    SUBTITLE(1),
    ACTIONBAR(2),
    TIMES(3),
    HIDE(4),
    RESET(5);

    private final int id;

    public static TitleAction getById(int id) {
        return stream(values())
                .filter((action) -> action.id == id)
                .findFirst()
                .orElse(TITLE);
    }

}