package pl.mrstudios.proxy.netty.packet.annotations;

import pl.mrstudios.proxy.netty.enums.MinecraftVersion;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface PacketMapping {
    int id();
    MinecraftVersion version();
}
