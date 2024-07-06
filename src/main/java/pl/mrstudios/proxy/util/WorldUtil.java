package pl.mrstudios.proxy.util;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.impl.UndefinedPacket;

import java.io.InputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static net.kyori.adventure.nbt.BinaryTagIO.unlimitedReader;
import static net.kyori.adventure.nbt.BinaryTagTypes.BYTE_ARRAY;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.UNKNOWN;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.values;
import static pl.mrstudios.proxy.util.StreamUtil.resourceAsStream;

public class WorldUtil {

    public static @NotNull Collection<UndefinedPacket> chunkDataPacketsFor(@NotNull MinecraftVersion minecraftVersion) {
        return chunkDataPacketRegistry.getOrDefault(minecraftVersion, emptyList());
    }

    protected static final Map<MinecraftVersion, Collection<UndefinedPacket>> chunkDataPacketRegistry = new EnumMap<>(MinecraftVersion.class);

    static {
        stream(values())
                .filter((minecraftVersion) -> minecraftVersion != UNKNOWN)
                .forEach((minecraftVersion) -> chunkDataPacketRegistry.put(minecraftVersion, prepareChunkPackets(
                        format("world/world_%s.nbt", minecraftVersion.getName().replace(".", "_")), minecraftVersion
                )));
    }

    protected static @NotNull Collection<UndefinedPacket> prepareChunkPackets(
            @NotNull String resourcePath,
            @NotNull MinecraftVersion minecraftVersion
    ) {

        try (InputStream inputStream = resourceAsStream(resourcePath)) {

            CompoundBinaryTag compoundBinaryTag = unlimitedReader().read(inputStream, BinaryTagIO.Compression.GZIP);

            return compoundBinaryTag.getList("data", BYTE_ARRAY).stream()
                    .map(ByteArrayBinaryTag.class::cast).map(ByteArrayBinaryTag::value)
                    .map((bytes) -> new UndefinedPacket(compoundBinaryTag.getInt("id"), bytes))
                    .toList();

        } catch (Exception exception) {
            throw new RuntimeException("Unable to create " + minecraftVersion.getName() + " chunk data packets.", exception);
        }

    }

}
