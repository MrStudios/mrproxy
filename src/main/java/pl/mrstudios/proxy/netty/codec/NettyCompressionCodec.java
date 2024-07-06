package pl.mrstudios.proxy.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.buffer.Buffer;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class NettyCompressionCodec extends ByteToMessageCodec<ByteBuf> {

    private final byte[] buffer;
    private final Inflater inflater;
    private final Deflater deflater;
    private int threshold;

    public NettyCompressionCodec(int thresholdIn) {
        this.threshold = thresholdIn;
        this.deflater = new Deflater();
        this.inflater = new Inflater();
        this.buffer = new byte[8192];
    }

    @Override
    protected void encode(
            @NotNull ChannelHandlerContext channelHandlerContext,
            @NotNull ByteBuf byteBufIn,
            @NotNull ByteBuf byteBufOut
    ) {

        int readable = byteBufIn.readableBytes();
        Buffer buffer = new Buffer(byteBufOut);
        if (readable < this.threshold) {
            buffer.writeVarInt(0);
            byteBufOut.writeBytes(byteBufIn);
            return;
        }

        byte[] bytes = new byte[readable];
        byteBufIn.readBytes(bytes);
        buffer.writeVarInt(bytes.length);

        this.deflater.setInput(bytes, 0, readable);
        this.deflater.finish();

        while (!this.deflater.finished()) {
            int length = this.deflater.deflate(this.buffer);
            buffer.writeBytes(this.buffer, length);
        }

        this.deflater.reset();

    }

    @Override
    protected void decode(
            @NotNull ChannelHandlerContext channelHandlerContext,
            @NotNull ByteBuf byteBuf,
            @NotNull List<Object> list
    ) throws Exception {

        if (byteBuf.readableBytes() == 0)
            return;

        Buffer buffer = new Buffer(byteBuf);
        int size = buffer.readVarInt();

        if (size == 0) {
            list.add(byteBuf.readBytes(byteBuf.readableBytes()));
            return;
        }

        if (size < this.threshold)
            throw new DecoderException("Badly compressed packet: size of " + size + " is below threshold of " + this.threshold + ".");

        if (size > 2097152)
            throw new DecoderException("Badly compressed packet: size of " + size + " is larger than protocol maximum of " + 2097152 + ".");

        byte[] inflated = new byte[size];
        byte[] bytes = new byte[byteBuf.readableBytes()];

        byteBuf.readBytes(bytes);
        this.inflater.setInput(bytes);
        this.inflater.inflate(inflated);

        list.add(wrappedBuffer(inflated));
        this.inflater.reset();

    }

    public void setCompressionThreshold(int threshold) {
        this.threshold = threshold;
    }

}