package pl.mrstudios.proxy.netty.packet.impl.handshake;

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

import static pl.mrstudios.proxy.netty.enums.ConnectionState.HANDSHAKE;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.*;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.SERVER;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketInformation(
        direction = SERVER,
        connectionState = HANDSHAKE,
        mappings = {
                @PacketMapping(id = 0x00, version = UNKNOWN),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_20_1)
        }
)
public class HandshakePacket implements Packet {

    private int protocolVersion;
    private String host;
    private int port;
    private int nextState;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        this.protocolVersion = buffer.readVarInt();
        this.host = buffer.readString(64);
        this.port = buffer.readShort();
        this.nextState = buffer.readVarInt();
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeVarInt(this.protocolVersion);
        buffer.writeString(this.host);
        buffer.writeShort(this.port);
        buffer.writeVarInt(this.nextState);
    }

}
