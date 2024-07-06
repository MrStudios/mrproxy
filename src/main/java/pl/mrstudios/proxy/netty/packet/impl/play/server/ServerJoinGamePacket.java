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

import java.util.Collection;

import static java.util.stream.IntStream.range;
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
                @PacketMapping(id = 0x24, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x26, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x28, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x28, version = MINECRAFT_1_20_1)
        }
)
public class ServerJoinGamePacket implements Packet {

    private int entityId;
    private boolean hardcore;
    private GameMode gameMode;
    private GameMode previousGameMode;
    private Collection<World> worlds;
    private CompoundBinaryTag dimensionCodec;
    private CompoundBinaryTag dimension;
    private World world;
    private long hashedSeed;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean debug;
    private boolean flat;

    /* 1.19.4+ */
    private String dimensionName;
    private String deathDimensionName;
    private Location deathLocation;

    /* 1.20.1+ */
    private int portalCooldown;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.entityId = buffer.readInt();
        this.hardcore = buffer.readBoolean();
        this.gameMode = GameMode.getById(buffer.readByte());
        this.previousGameMode = GameMode.getById(buffer.readByte());
        this.worlds = range(0, buffer.readVarInt())
                .mapToObj((i) -> buffer.readString())
                .map(World::new).toList();

        this.dimensionCodec = buffer.readCompoundTag();
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            this.dimensionName = buffer.readString();
        else
            this.dimension = buffer.readCompoundTag();

        this.world = new World(buffer.readString());
        this.hashedSeed = buffer.readLong();
        this.maxPlayers = buffer.readVarInt();
        this.viewDistance = buffer.readVarInt();
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2))
            this.simulationDistance = buffer.readVarInt();

        this.reducedDebugInfo = buffer.readBoolean();
        this.enableRespawnScreen = buffer.readBoolean();
        this.debug = buffer.readBoolean();
        this.flat = buffer.readBoolean();

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4) && buffer.readBoolean()) {
            this.deathDimensionName = buffer.readString();
            this.deathLocation = buffer.readLocation();
        }

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_20_1))
            this.portalCooldown = buffer.readVarInt();

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeInt(this.entityId);
        buffer.writeBoolean(this.hardcore);
        buffer.writeByte(this.gameMode.getId());
        buffer.writeByte(this.previousGameMode.getId());
        buffer.writeVarInt(this.worlds.size());
        for (World world : this.worlds)
            buffer.writeString(world.name());

        buffer.writeBinaryTag(this.dimensionCodec);

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            buffer.writeString(this.dimensionName);
        else
            buffer.writeBinaryTag(this.dimension);

        buffer.writeString(this.world.name());
        buffer.writeLong(this.hashedSeed);
        buffer.writeVarInt(this.maxPlayers);
        buffer.writeVarInt(this.viewDistance);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2))
            buffer.writeVarInt(this.simulationDistance);

        buffer.writeBoolean(this.reducedDebugInfo);
        buffer.writeBoolean(this.enableRespawnScreen);
        buffer.writeBoolean(this.debug);
        buffer.writeBoolean(this.flat);

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
