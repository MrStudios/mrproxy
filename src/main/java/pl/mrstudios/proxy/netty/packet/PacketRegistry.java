package pl.mrstudios.proxy.netty.packet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.commons.reflection.Reflections;
import pl.mrstudios.commons.reflection.exception.ReflectionScannerException;
import pl.mrstudios.proxy.netty.enums.ConnectionState;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.enums.PacketDirection;
import pl.mrstudios.proxy.netty.packet.annotations.PacketInformation;
import pl.mrstudios.proxy.netty.packet.annotations.PacketMapping;

import java.util.*;

public class PacketRegistry {
    private final Map<MinecraftVersion, Map<ConnectionState, Map<PacketDirection, Map<Integer, Packet>>>> packets;

    public PacketRegistry() {

        /* Initialize */
        this.packets = new EnumMap<>(MinecraftVersion.class);

        /* Insert Default Values */
        for (MinecraftVersion minecraftVersion : MinecraftVersion.values())
            this.packets.put(minecraftVersion, new EnumMap<>(ConnectionState.class));

        for (MinecraftVersion minecraftVersion : MinecraftVersion.values())
            for (ConnectionState connectionState : ConnectionState.values())
                this.packets.get(minecraftVersion).put(connectionState, new EnumMap<>(PacketDirection.class));

        for (MinecraftVersion minecraftVersion : MinecraftVersion.values())
            for (ConnectionState connectionState : ConnectionState.values())
                for (PacketDirection packetDirection : PacketDirection.values())
                    this.packets.get(minecraftVersion).get(connectionState).put(packetDirection, new HashMap<>());

        /* Register Packets */
        new Reflections<Packet>("pl.mrstudios.proxy")
                .getClassesAnnotatedWith(PacketInformation.class)
                .forEach((clazz) -> {

                    try {

                        PacketInformation packetInformation = clazz.getAnnotation(PacketInformation.class);
                        for (PacketMapping packetMapping : packetInformation.mappings())
                            this.packets.get(packetMapping.version())
                                    .get(packetInformation.connectionState())
                                    .get(packetInformation.direction())
                                    .put(packetMapping.id(), clazz.getDeclaredConstructor().newInstance());

                    } catch (Exception exception) {
                        throw new ReflectionScannerException("Unable to initialize " + clazz.getSimpleName() + " packet due to exception.", exception);
                    }

                });

    }

    public @NotNull Optional<PacketMapping> getPacketId(@NotNull Packet packet, @NotNull MinecraftVersion minecraftVersion) {
        return Arrays.stream(packet.getClass().getAnnotation(PacketInformation.class).mappings())
                .filter((mapping) -> mapping.version() == minecraftVersion)
                .findFirst();
    }

    public @Nullable Packet getPacket(int id, MinecraftVersion minecraftVersion, ConnectionState connectionState, PacketDirection packetDirection) {
        return this.packets.get(minecraftVersion)
                .get(connectionState)
                .get(packetDirection)
                .getOrDefault(id, null);
    }

    public @NotNull Packet newPacketInstance(int id, MinecraftVersion minecraftVersion, ConnectionState connectionState, PacketDirection packetDirection) throws Exception {
        return this.packets.get(minecraftVersion)
                .get(connectionState)
                .get(packetDirection)
                .get(id)
                .getClass()
                .getDeclaredConstructor()
                .newInstance();
    }

}
