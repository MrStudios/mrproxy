package pl.mrstudios.proxy.core.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.netty.packet.Packet;

public interface RemoteConnection {
    boolean isConnected();
    default void sendPacket(@NotNull Packet packet) {}
    default void disconnect() {}
    default void disconnect(@Nullable Packet packet, boolean silent) {}
}
