package pl.mrstudios.proxy.netty.packet.impl.play.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.player.ResourcePackAction;
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
                @PacketMapping(id = 0x21, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x21, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x24, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x24, version = MINECRAFT_1_20_1)
        }
)
public class ClientResourcePackResponsePacket implements Packet {

    private ResourcePackAction action;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        this.action = ResourcePackAction.getById(buffer.readVarInt());
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeVarInt(this.action.getId());
    }

}
