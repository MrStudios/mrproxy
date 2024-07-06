package pl.mrstudios.proxy.netty.packet.impl.status.server;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.status.ServerInfo;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import static pl.mrstudios.proxy.netty.enums.ConnectionState.STATUS;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.*;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.CLIENT;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketInformation(
        direction = CLIENT,
        connectionState = STATUS,
        mappings = {
                @PacketMapping(id = 0x00, version = UNKNOWN),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x00, version = MINECRAFT_1_20_1)
        }
)
public class ServerStatusResponsePacket implements Packet {

    private ServerInfo serverInfo;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        this.serverInfo = gson.fromJson(buffer.readString(), ServerInfo.class);
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeString(gson.toJson(this.serverInfo));
    }

    protected static final Gson gson = new Gson();

}
