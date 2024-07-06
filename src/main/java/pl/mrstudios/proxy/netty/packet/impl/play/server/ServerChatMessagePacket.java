package pl.mrstudios.proxy.netty.packet.impl.play.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.component.ChatMessageType;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import java.util.UUID;

import static pl.mrstudios.proxy.minecraft.component.ChatMessageType.getById;
import static pl.mrstudios.proxy.minecraft.component.ChatMessageType.*;
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
                @PacketMapping(id = 0x0E, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x0F, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x64, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x64, version = MINECRAFT_1_20_1)
        }
)
public class ServerChatMessagePacket implements Packet {

    private Component message;
    private ChatMessageType type;
    private UUID sender;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.message = buffer.readComponent();
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4)) {
            this.type = buffer.readBoolean() ? ACTIONBAR : SYSTEM;
        } else {
            this.type = getById(buffer.readByte());
            this.sender = buffer.readUniqueId();
        }

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeComponent(this.message);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4)) {
            buffer.writeBoolean(this.type == ACTIONBAR);
        } else {
            buffer.writeByte(this.type.getId());
            buffer.writeUniqueId(this.sender);
        }

    }

}
