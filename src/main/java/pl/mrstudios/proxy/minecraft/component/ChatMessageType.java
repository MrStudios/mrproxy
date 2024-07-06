package pl.mrstudios.proxy.minecraft.component;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum ChatMessageType {

    CHAT(0),
    SYSTEM(1),
    ACTIONBAR(2);

    private final int id;

    public static ChatMessageType getById(int id) {
        return stream(values())
                .filter((action) -> action.id == id)
                .findFirst()
                .orElse(CHAT);
    }

}
