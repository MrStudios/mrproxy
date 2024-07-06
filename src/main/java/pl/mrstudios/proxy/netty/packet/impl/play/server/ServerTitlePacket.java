package pl.mrstudios.proxy.netty.packet.impl.play.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.component.TitleAction;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import static pl.mrstudios.proxy.minecraft.component.TitleAction.getById;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.PLAY;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.MINECRAFT_1_16_5;
import static pl.mrstudios.proxy.netty.enums.PacketDirection.CLIENT;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketInformation(
        direction = CLIENT,
        connectionState = PLAY,
        mappings = {
                @PacketMapping(id = 0x4F, version = MINECRAFT_1_16_5)
        }
)
public class ServerTitlePacket implements Packet {

    private TitleAction titleAction;
    private Component component;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.titleAction = getById(buffer.readVarInt());
        switch (this.titleAction) {

            case TITLE, SUBTITLE, ACTIONBAR ->
                this.component = buffer.readComponent();

            case TIMES -> {
                this.fadeIn = buffer.readInt();
                this.stay = buffer.readInt();
                this.fadeOut = buffer.readInt();
            }

            default -> {}

        }

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeVarInt(this.titleAction.getId());
        switch (this.titleAction) {

            case TITLE, SUBTITLE, ACTIONBAR ->
                buffer.writeComponent(this.component);

            case TIMES -> {
                buffer.writeInt(this.fadeIn);
                buffer.writeInt(this.stay);
                buffer.writeInt(this.fadeOut);
            }

            default -> {}

        }

    }

}
