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
                @PacketMapping(id = 0x48, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x51, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x56, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x56, version = MINECRAFT_1_20_1)
        }
)
public class ServerSetExperiencePacket implements Packet {

    private float barFillPercentage;
    private int level;
    private int totalExperience;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        this.barFillPercentage = buffer.readFloat();
        this.level = buffer.readVarInt();
        this.totalExperience = buffer.readVarInt();
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeFloat(this.barFillPercentage);
        buffer.writeVarInt(this.level);
        buffer.writeVarInt(this.totalExperience);
    }

}
