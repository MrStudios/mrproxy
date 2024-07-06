package pl.mrstudios.proxy.util;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.minecraft.inventory.ItemStack;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.packet.impl.UndefinedPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerJoinGamePacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerRespawnPacket;

import static io.netty.buffer.ByteBufUtil.getBytes;
import static io.netty.buffer.Unpooled.buffer;
import static java.util.stream.Stream.of;
import static pl.mrstudios.proxy.minecraft.world.Dimension.THE_END;
import static pl.mrstudios.proxy.minecraft.world.Dimension.dimensionData;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.MINECRAFT_1_18_2;

public class ProtocolUtil {

    public static void dimensionSwitch(
            @NotNull User user,
            @NotNull ServerJoinGamePacket packet
    ) {
        of(
                new ServerRespawnPacket(
                        dimensionData(THE_END, user.getConnection().getMinecraftVersion()),
                        packet.getWorld(),
                        packet.getHashedSeed(),
                        packet.getGameMode(),
                        packet.getGameMode(),
                        packet.isDebug(),
                        packet.isFlat(),
                        false,
                        "minecraft:the_end",
                        (byte) 0x00,
                        null,
                        null,
                        0
                ),
                packet,
                new ServerRespawnPacket(
                        packet.getDimension(),
                        packet.getWorld(),
                        packet.getHashedSeed(),
                        packet.getGameMode(),
                        packet.getGameMode(),
                        packet.isDebug(),
                        packet.isFlat(),
                        false,
                        packet.getDimensionName(),
                        (byte) 0x00,
                        packet.getDeathDimensionName(),
                        packet.getDeathLocation(),
                        packet.getPortalCooldown()
                )
        ).forEach(user.getConnection()::sendPacket);
    }

    public static void openWindowPlayerPacket(
            @NotNull Connection connection,
            @NotNull Integer windowId,
            @NotNull Integer windowType,
            @NotNull Component windowTitle
    ) {

        Buffer buffer = new Buffer(buffer());

        buffer.writeVarInt(windowId);
        buffer.writeVarInt(windowType);
        buffer.writeComponent(windowTitle);

        connection.sendPacket(createUndefinedPacket(
                switch (connection.getMinecraftVersion()) {

                    case MINECRAFT_1_16_5 -> 0x2D;
                    case MINECRAFT_1_18_2 -> 0x2E;
                    case MINECRAFT_1_19_4, MINECRAFT_1_20_1 -> 0x30;
                    default ->
                            throw new IllegalStateException("Unexpected minecraft version, probably no mappings for that version.");

                },
                buffer
        ));

    }

    public static void setWindowItemPacket(
            @NotNull Connection connection,
            @NotNull Integer windowId,
            @NotNull Integer stateId,
            @NotNull Short slotNumber,
            @NotNull ItemStack itemStack
    ) {

        Buffer buffer = new Buffer(buffer());

        buffer.writeByte(windowId);
        if (connection.getMinecraftVersion().isNewerOrEqual(MINECRAFT_1_18_2))
            buffer.writeVarInt(stateId);

        buffer.writeShort(slotNumber);
        buffer.writeItemStack(itemStack);

        connection.sendPacket(createUndefinedPacket(
                switch (connection.getMinecraftVersion()) {

                    case MINECRAFT_1_16_5 -> 0x15;
                    case MINECRAFT_1_18_2 -> 0x16;
                    case MINECRAFT_1_19_4, MINECRAFT_1_20_1 -> 0x14;
                    default ->
                            throw new IllegalStateException("Unexpected minecraft version, probably no mappings for that version.");

                },
                buffer
        ));

    }

    public static void closeWindowPacket(
            @NotNull Connection connection,
            @NotNull Integer windowId
    ) {

        Buffer buffer = new Buffer(buffer());

        buffer.writeByte(windowId);

        connection.sendPacket(createUndefinedPacket(
                switch (connection.getMinecraftVersion()) {

                    case MINECRAFT_1_16_5 -> 0x12;
                    case MINECRAFT_1_18_2 -> 0x13;
                    case MINECRAFT_1_19_4 -> 0x0C;
                    case MINECRAFT_1_20_1 -> 0x11;
                    default ->
                            throw new IllegalStateException("Unexpected minecraft version, probably no mappings for that version.");

                },
                buffer
        ));

    }

    public static @NotNull UndefinedPacket createUndefinedPacket(
            @NotNull Integer identifier,
            @NotNull ByteBuf data
    ) {
        return new UndefinedPacket(identifier, getBytes(data));
    }

}
