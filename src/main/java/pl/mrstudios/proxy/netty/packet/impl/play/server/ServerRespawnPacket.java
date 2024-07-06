package pl.mrstudios.proxy.netty.packet.impl.play.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.entity.Location;
import pl.mrstudios.proxy.minecraft.player.GameMode;
import pl.mrstudios.proxy.minecraft.world.World;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import static pl.mrstudios.proxy.minecraft.player.GameMode.getById;
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
                @PacketMapping(id = 0x39, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x3D, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x41, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x41, version = MINECRAFT_1_20_1)
        }
)
public class ServerRespawnPacket implements Packet {

    private CompoundBinaryTag dimension;
    private World world;
    private long hashedSeed;
    private GameMode gameMode;
    private GameMode previousGameMode;
    private boolean debug;
    private boolean flat;
    private boolean copyMetadata;

    /* 1.19.4+ */
    private String dimensionName;
    private byte dataKept;
    private String deathDimensionName;
    private Location deathLocation;

    /* 1.20.1+ */
    private int portalCooldown;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {


        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            this.dimensionName = buffer.readString();
        else
            this.dimension = buffer.readCompoundTag();

        this.world = new World(buffer.readString());
        this.hashedSeed = buffer.readLong();
        this.gameMode = getById(buffer.readVarInt());
        this.previousGameMode = getById(buffer.readVarInt());
        this.debug = buffer.readBoolean();
        this.flat = buffer.readBoolean();
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            this.dataKept = buffer.readByte();
        else
            this.copyMetadata = buffer.readBoolean();

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4) && buffer.readBoolean()) {
            this.deathDimensionName = buffer.readString();
            this.deathLocation = buffer.readLocation();
        }

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_20_1))
            this.portalCooldown = buffer.readVarInt();

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            buffer.writeString(this.dimensionName);
        else
            buffer.writeBinaryTag(this.dimension);

        buffer.writeString(this.world.name());
        buffer.writeLong(this.hashedSeed);
        buffer.writeVarInt(this.gameMode.getId());
        buffer.writeVarInt(this.previousGameMode.getId());
        buffer.writeBoolean(this.debug);
        buffer.writeBoolean(this.flat);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            buffer.writeByte(this.dataKept);
        else
            buffer.writeBoolean(this.copyMetadata);

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4)) {
            buffer.writeBoolean(this.deathDimensionName != null);
            if (this.deathDimensionName != null) {
                buffer.writeString(this.deathDimensionName);
                buffer.writeLocation(this.deathLocation);
            }
        }

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_20_1))
            buffer.writeVarInt(this.portalCooldown);

    }

}
