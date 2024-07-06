package pl.mrstudios.proxy.minecraft.entity;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location extends OkaeriConfig {
    private double x, y, z;
    private float yaw, pitch;
}
