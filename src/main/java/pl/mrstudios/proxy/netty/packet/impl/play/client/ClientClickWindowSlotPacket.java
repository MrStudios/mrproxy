package pl.mrstudios.proxy.netty.packet.impl.play.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.minecraft.inventory.ItemStack;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.IntStream.range;
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
                @PacketMapping(id = 0x09, version = MINECRAFT_1_16_5),
                @PacketMapping(id = 0x08, version = MINECRAFT_1_18_2),
                @PacketMapping(id = 0x0B, version = MINECRAFT_1_19_4),
                @PacketMapping(id = 0x0B, version = MINECRAFT_1_20_1)
        }
)
public class ClientClickWindowSlotPacket implements Packet {

    private int windowId;
    private int stateId;
    private short slot;
    private int button;
    private int mode;
    private Map<Short, ItemStack> slotData;
    private ItemStack heldItem;

    @Override
    public void read(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        this.windowId = buffer.readByte();

        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2))
            this.stateId = buffer.readVarInt();

        this.slot = buffer.readShort();
        this.button = buffer.readByte();
        if (minecraftVersion.equals(MINECRAFT_1_16_5))
            this.stateId = buffer.readShort();

        this.mode = buffer.readVarInt();
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2)) {
            this.slotData = new HashMap<>();
            range(0, buffer.readVarInt())
                    .forEach((i) -> this.slotData.put(
                            buffer.readShort(), buffer.readItemStack()
                    ));
        }

        this.heldItem = buffer.readItemStack();

    }

    @Override
    public void write(@NotNull Buffer buffer, @NotNull MinecraftVersion minecraftVersion) {

        buffer.writeByte(this.windowId);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2))
            buffer.writeVarInt(this.stateId);

        buffer.writeShort(this.slot);
        buffer.writeByte(this.button);
        if (minecraftVersion.equals(MINECRAFT_1_16_5))
            buffer.writeShort(this.stateId);

        buffer.writeVarInt(this.mode);
        if (minecraftVersion.isNewerOrEqual(MINECRAFT_1_18_2)) {
            buffer.writeVarInt(this.slotData.size());
            this.slotData.forEach((slot, itemStack) -> {
                buffer.writeShort(slot);
                buffer.writeItemStack(itemStack);
            });
        }

        buffer.writeItemStack(this.heldItem);

    }

}
