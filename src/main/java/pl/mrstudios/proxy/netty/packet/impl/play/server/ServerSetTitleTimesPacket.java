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
                @PacketMapping(id = 0x5B, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x60, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x60, version = MINECRAFT_1_20_1)
        }
)
public class ServerSetTitleTimesPacket implements Packet {

    private int fadeIn;
    private int stay;
    private int fadeOut;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        this.fadeIn = buffer.readInt();
        this.stay = buffer.readInt();
        this.fadeOut = buffer.readInt();
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeInt(this.fadeIn);
        buffer.writeInt(this.stay);
        buffer.writeInt(this.fadeOut);
    }

}
