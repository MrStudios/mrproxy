package pl.mrstudios.proxy.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.Application;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;

public class StreamUtil {

    public static boolean saveResource(
            @NotNull Path path,
            @NotNull String resource
    ) {

        try {

            if (path.toFile().exists())
                return false;

            if (!path.toFile().exists())
                createDirectories(path.getParent());

            InputStream inputStream = StreamUtil.class.getClassLoader().getResourceAsStream(resource);
            if (inputStream == null)
                throw new NullPointerException("Unable to find resource.");

            copy(inputStream, path);
            return true;

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }

    public static byte[] writeString(@NotNull ByteBuf byteBuf, @NotNull String string) {
        writeVarInt(byteBuf, ByteBufUtil.utf8Bytes(string));
        byteBuf.writeCharSequence(string, StandardCharsets.UTF_8);
        return byteBuf.array();
    }

    public static void writeVarInt(@NotNull ByteBuf byteBuf, @NotNull Integer input) {
        while ((input & -128) != 0) {
            byteBuf.writeByte(input & 127 | 128);
            input >>>= 7;
        }
        byteBuf.writeByte(input);
    }

    public static InputStream resourceAsStream(@NotNull String path) {
        return Application.class.getClassLoader().getResourceAsStream(path);
    }

}
