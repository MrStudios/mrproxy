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
                @PacketMapping(id = 0x34, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x38, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x3C, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x3C, version = MINECRAFT_1_20_1)
        }
)
public class ServerPlayerPositionAndLookPacket implements Packet {

    private Location location;
    private byte flags;
    private int teleportId;
    private boolean dismountVehicle;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.location = new Location(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readFloat(),
                buffer.readFloat()
        );
        this.flags = buffer.readByte();
        this.teleportId = buffer.readVarInt();

        if (minecraftVersion == MINECRAFT_1_18_2)
            this.dismountVehicle = buffer.readBoolean();

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeDouble(this.location.getX());
        buffer.writeDouble(this.location.getY());
        buffer.writeDouble(this.location.getZ());
        buffer.writeFloat(this.location.getYaw());
        buffer.writeFloat(this.location.getPitch());
        buffer.writeByte(this.flags);
        buffer.writeVarInt(this.teleportId);

        if (minecraftVersion == MINECRAFT_1_18_2)
            buffer.writeBoolean(this.dismountVehicle);

    }

}
