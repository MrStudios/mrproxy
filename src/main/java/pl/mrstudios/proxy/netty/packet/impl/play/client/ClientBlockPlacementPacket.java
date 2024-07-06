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
                @PacketMapping(id = 0x2E, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x2E, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x31, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x31, version = MINECRAFT_1_20_1)
        }
)
public class ClientBlockPlacementPacket implements Packet {

    private int hand;
    private Location blockLocation;
    private int face;
    private float cursorX;
    private float cursorY;
    private float cursorZ;
    private boolean insideBlock;

    /* 1.19.4+ */
    private int sequence;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.hand = buffer.readVarInt();
        this.blockLocation = buffer.readLocation();
        this.face = buffer.readVarInt();
        this.cursorX = buffer.readFloat();
        this.cursorY = buffer.readFloat();
        this.cursorZ = buffer.readFloat();
        this.insideBlock = buffer.readBoolean();
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            this.sequence = buffer.readVarInt();

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeVarInt(this.hand);
        buffer.writeLocation(this.blockLocation);
        buffer.writeVarInt(this.face);
        buffer.writeFloat(this.cursorX);
        buffer.writeFloat(this.cursorY);
        buffer.writeFloat(this.cursorZ);
        buffer.writeBoolean(this.insideBlock);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            buffer.writeVarInt(this.sequence);

    }

    public @NotNull Location getBlockLocation() {

        long positionValue = ((long) this.blockLocation.getX() & 0x3FFFFFF) << 38 | ((long) this.blockLocation.getY() & 0xFFF) << 26 | (long) this.blockLocation.getZ() & 0x3FFFFFF;
        double x = positionValue >> 38, y = positionValue << 52 >> 52, z = positionValue << 26 >> 38;

        return new Location(x, y, z, this.blockLocation.getYaw(), this.blockLocation.getPitch());

    }

}
