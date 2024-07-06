package pl.mrstudios.proxy.minecraft.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class VersionInfo {
    private String name;
    private int protocol;
}
