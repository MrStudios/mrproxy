package pl.mrstudios.proxy.netty.packet.impl.login.client;

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

import java.util.UUID;

import static pl.mrstudios.proxy.netty.enums.ConnectionState.LOGIN;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.*;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.SERVER;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketInformation(
        direction = SERVER,
        connectionState = LOGIN,
        mappings = {
                @PacketMapping(id = 0x00, version = UNKNOWN),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_20_1)
        }
)
public class ClientLoginStartPacket implements Packet {

    private String name;

    /* 1.19.4+ */
    private UUID uniqueId;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.name = buffer.readString(16);

        if (minecraftVersion == MinecraftVersion.UNKNOWN) {
            buffer.clear();
            return;
        }

        if (!minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            return;

        if (buffer.readBoolean())
            this.uniqueId = buffer.readUniqueId();

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeString(this.name);

        if (!minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            return;

        buffer.writeBoolean(this.uniqueId != null);
        if (this.uniqueId != null)
            buffer.writeUniqueId(this.uniqueId);

    }

}
