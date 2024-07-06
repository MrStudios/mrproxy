package pl.mrstudios.proxy.minecraft.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.mrstudios.proxy.minecraft.profile.GameProfile;

import java.util.Collection;

@Getter @Setter
@AllArgsConstructor
public class PlayerInfo {
    private int max;
    private int online;
    private Collection<GameProfile> sample;
}
