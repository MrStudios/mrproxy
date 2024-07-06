package pl.mrstudios.proxy.netty.packet.impl.login.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.profile.GameProfile;
import pl.mrstudios.proxy.minecraft.property.Property;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import static pl.mrstudios.proxy.netty.enums.ConnectionState.LOGIN;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.*;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.CLIENT;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketInformation(
        direction = CLIENT,
        connectionState = LOGIN,
        mappings = {
                @PacketMapping(id = 0x02, version = UNKNOWN),
                @PacketMapping(id = 0x02, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x02, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x02, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x02, version = MINECRAFT_1_20_1)
        }
)
public class ServerLoginSuccessPacket implements Packet {

    private GameProfile gameProfile;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.gameProfile = new GameProfile(buffer.readUniqueId(), buffer.readString(16));
        if (!minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            return;

        for (int i = 0; i < buffer.readVarInt(); i++)
            this.gameProfile.getProperties().add(new Property(
                    buffer.readString(),
                    buffer.readString(),
                    (buffer.readBoolean()) ? buffer.readString() : null
            ));

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeUniqueId(this.gameProfile.getId());
        buffer.writeString(this.gameProfile.getName());
        if (!minecraftVersion.isNewerOrEqual(MINECRAFT_1_19_4))
            return;

        buffer.writeVarInt(this.gameProfile.getProperties().size());
        this.gameProfile.getProperties()
                .forEach((name, property) -> {

                    buffer.writeString(property.name());
                    buffer.writeString(property.value());
                    buffer.writeBoolean(property.signature() != null);
                    if (property.signature() != null)
                        buffer.writeString(property.signature());

                });

    }

}
