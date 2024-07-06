package pl.mrstudios.proxy.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.netty.buffer.Buffer;
import pl.mrstudios.proxy.netty.enums.ConnectionState;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.enums.PacketDirection;
import pl.mrstudios.proxy.netty.packet.Packet;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;
import pl.mrstudios.proxy.netty.packet.impl.UndefinedPacket;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NettyPacketCodec extends ByteToMessageCodec<Packet> {

    private PacketRegistry packetRegistry;
    private MinecraftVersion minecraftVersion;

    private ConnectionState connectionState;
    private PacketDirection packetDirection;

    @Override
    protected void encode(
            @NotNull ChannelHandlerContext channelHandlerContext,
            @NotNull Packet packet,
            @NotNull ByteBuf byteBuf
    ) {

        Buffer buffer = new Buffer(byteBuf);

        if (packet instanceof UndefinedPacket undefinedPacket) {
            buffer.writeVarInt(undefinedPacket.getId());
            buffer.writeBytes(undefinedPacket.getBytes());
            return;
        }

        this.packetRegistry.getPacketId(packet, this.minecraftVersion)
                .map(PacketMapping::id)
                .ifPresent(buffer::writeVarInt);

        packet.write(buffer, this.minecraftVersion);
        packet = null;

    }

    @Override
    @SuppressWarnings("unchecked")
    protected void decode(
            @NotNull ChannelHandlerContext channelHandlerContext,
            @NotNull ByteBuf byteBuf,
            @NotNull List list
    ) {

        if (!byteBuf.isReadable())
            return;

        Buffer buffer = new Buffer(byteBuf);

        int id = buffer.readVarInt();
        Packet packet = this.packetRegistry.getPacket(id, this.minecraftVersion, this.connectionState, this.packetDirection);

        if (packet == null) {

            byte[] data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);

            packet = new UndefinedPacket(id, data);

        } else packet.read(buffer, this.minecraftVersion);

        if (buffer.isReadable())
            throw new DecoderException(String.format("Packet (%s) was larger than i expected found %s bytes extra.", packet.getClass().getSimpleName(), buffer.readableBytes()));

        list.add(packet);
        byteBuf.clear();
        packet = null;

    }

}
