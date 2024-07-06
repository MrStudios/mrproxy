package pl.mrstudios.proxy.netty.packet.impl.play.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
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
                @PacketMapping(id = 0x5A, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x5F, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x5F, version = MINECRAFT_1_20_1)
        }
)
public class ServerSetTitlePacket implements Packet {

    private Component component;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        this.component = buffer.readComponent();
    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {
        buffer.writeComponent(this.component);
    }

}
