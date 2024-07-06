package pl.mrstudios.proxy.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.buffer.Buffer;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class NettyFrameCodec extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(
            @NotNull ChannelHandlerContext channelHandlerContext,
            @NotNull ByteBuf byteBuf,
            @NotNull ByteBuf byteBuf2
    ) {

        int size = byteBuf.readableBytes(),
                frameSize = varIntSize(size);

        if (frameSize > 3)
            throw new IllegalArgumentException("Unable to fit " + size + " into 3 bytes.");

        Buffer buffer = new Buffer(byteBuf2);

        buffer.ensureWritable(frameSize + size);
        buffer.writeVarInt(size);
        buffer.writeBytes(byteBuf, byteBuf.readerIndex(), size);

    }

    @Override
    protected void decode(
            @NotNull ChannelHandlerContext channelHandlerContext,
            @NotNull ByteBuf byteBuf,
            @NotNull List<Object> list
    ) {

        byteBuf.markReaderIndex();
        byte[] bytes = new byte[3];

        for (int i = 0; i < bytes.length; i++) {

            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }

            bytes[i] = byteBuf.readByte();

            if (bytes[i] >= 0) {

                Buffer buffer = new Buffer(wrappedBuffer(bytes));
                try {

                    int frameSize = buffer.readVarInt();
                    if (byteBuf.readableBytes() >= frameSize) {
                        list.add(byteBuf.readBytes(frameSize));
                        return;
                    }

                    byteBuf.resetReaderIndex();

                } finally {
                    buffer.release();
                }

                return;

            }

        }

        throw new CorruptedFrameException("Packet length is wider than 21 bits.");

    }

    protected static int varIntSize(int input) {
        for (int i = 1; i < 5; i++)
            if ((input & -1 << i * 7) == 0)
                return i;

        return 5;
    }

}
