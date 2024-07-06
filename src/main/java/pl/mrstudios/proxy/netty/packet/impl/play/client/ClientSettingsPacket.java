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
                @PacketMapping(id = 0x05, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x05, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x08, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x08, version = MINECRAFT_1_20_1)
        }
)
public class ClientSettingsPacket implements Packet {

    private String locale;
    private byte viewDistance;
    private int chatMode;
    private boolean chatColors;
    private byte displayedSkinParts;
    private int mainHand;
    private boolean enableTextFiltering;
    private boolean allowServerListings;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.locale = buffer.readString(16);
        this.viewDistance = buffer.readByte();
        this.chatMode = buffer.readVarInt();
        this.chatColors = buffer.readBoolean();
        this.displayedSkinParts = buffer.readByte();
        this.mainHand = buffer.readVarInt();
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2)) {
            this.enableTextFiltering = buffer.readBoolean();
            this.allowServerListings = buffer.readBoolean();
        }

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeString(this.locale);
        buffer.writeByte(this.viewDistance);
        buffer.writeVarInt(this.chatMode);
        buffer.writeBoolean(this.chatColors);
        buffer.writeByte(this.displayedSkinParts);
        buffer.writeVarInt(this.mainHand);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2)) {
            buffer.writeBoolean(this.enableTextFiltering);
            buffer.writeBoolean(this.allowServerListings);
        }

    }

}
