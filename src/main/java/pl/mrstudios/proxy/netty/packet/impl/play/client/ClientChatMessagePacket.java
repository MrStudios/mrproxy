package pl.mrstudios.proxy.netty.packet.impl.play.client;

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

import java.util.BitSet;

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
                @PacketMapping(id = 0x03, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x03, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x05, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x05, version = MINECRAFT_1_20_1)
        }
)
public class ClientChatMessagePacket implements Packet {

    private String message;

    /* 1.19.4+ */
    private long timestamp;
    private long salt;
    private byte[] signature;
    private int messageCount;
    private BitSet bitSet;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.message = buffer.readString(256);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4)) {

            this.timestamp = buffer.readLong();
            this.salt = buffer.readLong();
            if (buffer.readBoolean())
                this.signature = buffer.readByteArray(256);

            this.messageCount = buffer.readVarInt();
            this.bitSet = buffer.readBitSet(20);

        }

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeString(this.message);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4)) {

            buffer.writeLong(this.timestamp);
            buffer.writeLong(this.salt);
            buffer.writeBoolean(this.signature != null);
            if (this.signature != null)
                buffer.writeByteArray(this.signature);

            buffer.writeVarInt(this.messageCount);
            buffer.writeBitSet(this.bitSet, 20);

        }

    }

}
