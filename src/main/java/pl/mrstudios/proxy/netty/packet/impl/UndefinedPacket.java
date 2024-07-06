package pl.mrstudios.proxy.netty.packet.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UndefinedPacket implements Packet {

    private int id;
    private byte[] bytes;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        throw new UnsupportedOperationException("Unable to read undefined packet with " + this.id + " identifier for " + minecraftVersion + " version.");
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeBytes(this.bytes);
    }

}
