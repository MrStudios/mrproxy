package pl.mrstudios.proxy.core.listener;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.commons.reflection.Reflections;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.ProxyUserJoinedEvent;
import pl.mrstudios.proxy.event.impl.RemotePacketReceivedEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerChatSuggestionsPacket;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerJoinGamePacket;

import java.util.Collection;
import java.util.Objects;

import static java.util.Arrays.stream;
import static pl.mrstudios.proxy.netty.enums.MinecraftVersion.MINECRAFT_1_19_4;

public class RemoteConnectionPacketListener implements Listener {

    private final Collection<String> commandSuggestions;

    @Inject
    public RemoteConnectionPacketListener() {
        this.commandSuggestions = new Reflections<>("pl.mrstudios.proxy")
                .getClassesAnnotatedWith(CommandDescription.class).stream()
                .filter(Objects::nonNull).filter((clazz) -> clazz.isAnnotationPresent(CommandDescription.class))
                .map((clazz) -> clazz.getAnnotation(CommandDescription.class)).map((annotation) -> {

                    StringBuilder stringBuilder = new StringBuilder();

                    stringBuilder.append(",").append(annotation.name());
                    stream(annotation.parameters())
                            .forEach((parameter) -> stringBuilder.append(" <")
                                    .append(parameter.name())
                                    .append(">")
                            );

                    return stringBuilder;

                }).map(StringBuilder::toString)
                .toList();
    }

    @EventHandler
    public void onJoinPacketReceived(@NotNull RemotePacketReceivedEvent event) {

        if (!(event.packet() instanceof ServerJoinGamePacket))
            return;

        if (!event.user().getConnection().getMinecraftVersion().isNewerOrEqual(MINECRAFT_1_19_4))
            return;

        event.user().getConnection().sendPacket(new ServerChatSuggestionsPacket(2, this.commandSuggestions));

    }

    @EventHandler
    public void onUserJoin(@NotNull ProxyUserJoinedEvent event) {

        if (!event.user().getConnection().getMinecraftVersion().isNewerOrEqual(MINECRAFT_1_19_4))
            return;

        event.user().getConnection().sendPacket(new ServerChatSuggestionsPacket(2, this.commandSuggestions));

    }

}
