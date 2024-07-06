package pl.mrstudios.proxy.netty.packet.impl.play.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import static pl.mrstudios.proxy.netty.enums.ConnectionState.PLAY;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.*;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.CLIENT;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketInformation(
        direction = CLIENT,
        connectionState = PLAY,
        mappings = {
                @PacketMapping(id = 0x30, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x32, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x34, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x34, version = MINECRAFT_1_20_1)
        }
)
public class ServerPlayerAbilitiesPacket implements Packet {

    private byte flags;
    private float flySpeed;
    private float fovModifier;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        this.flags = buffer.readByte();
        this.flySpeed = buffer.readFloat();
        this.fovModifier = buffer.readFloat();
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeByte(this.flags);
        buffer.writeFloat(this.flySpeed);
        buffer.writeFloat(this.fovModifier);
    }

}
