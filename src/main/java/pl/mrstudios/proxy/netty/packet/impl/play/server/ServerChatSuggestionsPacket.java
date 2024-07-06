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

import java.util.Collection;

import static java.util.stream.IntStream.range;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.PLAY;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.MINECRAFT_1_19_4;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.MINECRAFT_1_20_1;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.CLIENT;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketInformation(
        direction = CLIENT,
        connectionState = PLAY,
        mappings = {
                @PacketMapping(id = 0x16, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x16, version = MINECRAFT_1_20_1)
        }
)
public class ServerChatSuggestionsPacket implements Packet {

    private int action;
    private Collection<String> suggestions;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.action = buffer.readVarInt();
        this.suggestions = range(0, buffer.readVarInt())
                .mapToObj((i) -> buffer.readString()).toList();

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeVarInt(this.action);
        buffer.writeVarInt(this.suggestions.size());
        this.suggestions.forEach(buffer::writeString);

    }

}
