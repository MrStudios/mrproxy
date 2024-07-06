package pl.mrstudios.proxy.netty.packet.annotations;

import pl.mrstudios.proxy.netty.enums.ConnectionState;
import pl.mrstudios.proxy.netty.enums.PacketDirection;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface PacketInformation {

    PacketDirection direction();
    ConnectionState connectionState();
    PacketMapping[] mappings();

}
