package pl.mrstudios.proxy.minecraft.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.mrstudios.proxy.minecraft.property.PropertyMap;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class GameProfile {

    private UUID id;
    private String name;
    private PropertyMap properties;

    public GameProfile(UUID id, String name) {
        this(id, name, new PropertyMap());
    }

}
