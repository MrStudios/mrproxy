package pl.mrstudios.proxy.netty.packet.impl.status.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import static pl.mrstudios.proxy.netty.enums.ConnectionState.STATUS;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.*;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.SERVER;

@Getter @Setter
@NoArgsConstructor
@PacketInformation(
        direction = SERVER,
        connectionState = STATUS,
        mappings = {
                @PacketMapping(id = 0x00, version = UNKNOWN),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_20_1)
        }
)
public class ClientStatusRequestPacket implements Packet {

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {}

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {}

}
