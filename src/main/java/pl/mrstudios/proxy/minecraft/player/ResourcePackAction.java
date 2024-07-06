package pl.mrstudios.proxy.minecraft.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum ResourcePackAction {

    LOADED(0),
    DECLINED(1),
    FAILED_DOWNLOAD(2),
    ACCEPTED(3);

    private final int id;

    public static ResourcePackAction getById(int id) {
        return stream(values())
                .filter((gameMode) -> gameMode.id == id)
                .findFirst()
                .orElse(LOADED);
    }

}
