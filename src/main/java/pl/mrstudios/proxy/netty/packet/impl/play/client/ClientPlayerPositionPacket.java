package pl.mrstudios.proxy.netty.packet.impl.play.client;

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
import static pl.mrstudios.proxy.netty.enums.PacketDirection.SERVER;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketInformation(
        direction = SERVER,
        connectionState = PLAY,
        mappings = {
                @PacketMapping(id = 0x12, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x11, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x14, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x14, version = MINECRAFT_1_20_1)
        }
)
public class ClientPlayerPositionPacket implements Packet {

    private Location location;
    private boolean ground;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        this.location = new Location(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                0, 0
        );
        this.ground = buffer.readBoolean();
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeDouble(this.location.getX());
        buffer.writeDouble(this.location.getY());
        buffer.writeDouble(this.location.getZ());
        buffer.writeBoolean(this.ground);
    }

}
