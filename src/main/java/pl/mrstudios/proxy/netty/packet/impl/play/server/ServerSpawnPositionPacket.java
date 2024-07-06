package pl.mrstudios.proxy.netty.packet.impl.play.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.entity.Location;
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
                @PacketMapping(id = 0x42, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x4B, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x50, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x50, version = MINECRAFT_1_20_1)
        }
)
public class ServerSpawnPositionPacket implements Packet {

    private Location location;
    private float angle;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.location = buffer.readLocation();
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2))
            this.angle = buffer.readFloat();

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeLocation(this.location);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2))
            buffer.writeFloat(this.angle);

    }

}
