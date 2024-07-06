package pl.mrstudios.proxy.minecraft.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum InventoryAction {

    CLICK_ITEM(0),
    SHIFT_CLICK_ITEM(1),
    MOVE_TO_HOTBAR_SLOT(2),
    CREATIVE_GRAB_MAX_STACK(3),
    DROP_ITEM(4),
    SPREAD_ITEM(5),
    FILL_STACK(6);

    private final int id;

    public static InventoryAction getById(int id) {
        return stream(values())
                .filter((action) -> action.id == id)
                .findFirst()
                .orElse(CLICK_ITEM);
    }

}