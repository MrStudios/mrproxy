package pl.mrstudios.proxy.netty.packet;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;

public interface Packet {

    void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion);
    void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion);

}
