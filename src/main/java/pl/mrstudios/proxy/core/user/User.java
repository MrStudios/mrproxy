package pl.mrstudios.proxy.core.user;

import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.core.account.Account;
import pl.mrstudios.proxy.core.connection.RemoteConnection;
import pl.mrstudios.proxy.core.language.Language;
import pl.mrstudios.proxy.core.user.entity.Bot;
import pl.mrstudios.proxy.minecraft.component.TitleAction;
import pl.mrstudios.proxy.minecraft.entity.Location;
import pl.mrstudios.proxy.minecraft.world.World;
import pl.mrstudios.proxy.netty.connection.Connection;
import pl.mrstudios.proxy.netty.enums.MinecraftVersion;
import pl.mrstudios.proxy.netty.packet.impl.play.server.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

import static java.time.Duration.ofMillis;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Stream.of;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.title.Title.Times.times;
import static net.kyori.adventure.title.Title.title;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.ENGLISH;
import static pl.mrstudios.proxy.minecraft.component.ChatMessageType.ACTIONBAR;
import static pl.mrstudios.proxy.minecraft.component.ChatMessageType.SYSTEM;
import static pl.mrstudios.proxy.minecraft.player.GameMode.SURVIVAL;
import static pl.mrstudios.proxy.minecraft.player.GameMode.UNKNOWN;
import static pl.mrstudios.proxy.minecraft.world.Dimension.*;
import static pl.mrstudios.proxy.util.ProtocolUtil.dimensionSwitch;
import static pl.mrstudios.proxy.util.StreamUtil.writeString;
import static pl.mrstudios.proxy.util.StringUtil.applyLegacyColors;
import static pl.mrstudios.proxy.util.WorldUtil.chunkDataPacketsFor;

@Getter @Setter
@NoArgsConstructor
public class User implements Audience {

    private Account account;
    private Connection connection;
    private RemoteConnection remoteConnection;

    private List<Bot> bots;

    private boolean connected;
    private boolean motherEnabled;

    private ScheduledFuture<?> scheduledFuture;

    public User(@NotNull Account account, @NotNull Connection connection) {
        this.account = account;
        this.connection = connection;
        this.connected = false;
        this.motherEnabled = false;
        this.bots = new ArrayList<>();
    }

    public @NotNull String getName() {
        return this.connection.getGameProfile().getName();
    }

    public @NotNull Language getLanguage() {
        return ofNullable(this.account.getSettings().language)
                .orElse(ENGLISH)
                .getLanguage();
    }

    /* Disconnect */
    public void disconnect(@NotNull Component reason) {
        this.connection.disconnect(reason);
    }

    /* Audience */
    public void sendTitle(
            @NotNull String title, @NotNull String subtitle,
            int fadeIn, int stay, int fadeOut
    ) {
        this.sendTitle(title(
                miniMessage().deserialize(title),
                miniMessage().deserialize(subtitle),
                times(ofMillis(fadeIn), ofMillis(stay), ofMillis(fadeOut))
        ));
    }

    public void sendTitle(@NotNull Title title) {

        if (this.connection.getMinecraftVersion().isNewerOrEqual(MinecraftVersion.MINECRAFT_1_18_2)) {

            of(
                    new ServerSetTitlePacket(title.title()),
                    new ServerSetSubTitlePacket(title.subtitle())
            ).forEach(this.connection::sendPacket);

            ofNullable(title.times())
                    .ifPresent((times) -> this.connection.sendPacket(new ServerSetTitleTimesPacket(
                            (int) times.fadeIn().toMillis() / 50,
                            (int) times.stay().toMillis() / 50,
                            (int) times.fadeOut().toMillis() / 50
                    )));

            return;

        }

        this.connection.sendPacket(new ServerTitlePacket(TitleAction.TITLE, title.title(), 0, 0, 0));
        this.connection.sendPacket(new ServerTitlePacket(TitleAction.SUBTITLE, title.subtitle(), 0, 0, 0));
        ofNullable(title.times())
                .ifPresent((times) -> this.connection.sendPacket(new ServerTitlePacket(
                        TitleAction.TIMES, Component.empty(),
                        (int) times.fadeIn().toMillis() / 50,
                        (int) times.stay().toMillis() / 50,
                        (int) times.fadeOut().toMillis() / 50
                )));

    }

    public void sendActionBar(@NotNull String string, @Nullable Object... args) {
        this.connection.sendPacket(new ServerChatMessagePacket(
                miniMessage().deserialize(String.format(string, args)), ACTIONBAR, EMPTY
        ));
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        this.connection.sendPacket(new ServerChatMessagePacket(message, ACTIONBAR, EMPTY));
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        this.connection.sendPacket(new ServerChatMessagePacket(
                message, SYSTEM, EMPTY
        ));
    }

    public void sendMessage(@NotNull String message) {
        this.connection.sendPacket(new ServerChatMessagePacket(
                miniMessage().deserialize(message), SYSTEM, EMPTY
        ));
    }

    public void sendMessage(@NotNull String message, @Nullable Object... args) {
        this.connection.sendPacket(new ServerChatMessagePacket(
                miniMessage().deserialize(String.format(message, args)), SYSTEM, EMPTY
        ));
    }

    /* Lobby */
    public void lobby(boolean dimensionSwitch) {

        ServerJoinGamePacket joinGamePacket = new ServerJoinGamePacket(
                -1,
                false,
                SURVIVAL,
                UNKNOWN,
                List.of(
                        new World("world"),
                        new World("world_the_nether"),
                        new World("world_the_end")
                ),
                dimensionCodec(this.connection.getMinecraftVersion()),
                dimensionData(THE_END, this.connection.getMinecraftVersion()),
                new World("minecraft:world_the_end"),
                current().nextLong(),
                1,
                8,
                8,
                false,
                false,
                false,
                false,
                THE_END.getKey(),
                null,
                null,
                0
        );

        if (dimensionSwitch)
            dimensionSwitch(this, joinGamePacket);
        else
            this.connection.sendPacket(joinGamePacket);

        Stream.of(
                new ServerPluginMessagePacket("minecraft:brand", writeString(Unpooled.buffer(), applyLegacyColors("&r &3&lᴍʀᴘʀᴏxʏ &8(www.mrproxy.net) &r"))),
                new ServerSpawnPositionPacket(new Location(
                        0.5, 64, 0.5, 0, 0
                ), 0),
                new ServerPlayerPositionAndLookPacket(
                        new Location(0.5, 64, 0.5, 0, 0),
                        (byte) 0x00, 1, false
                ),
                new ServerPlayerAbilitiesPacket((byte) 0x00, 0.05f, 0.1f)
        ).forEach(this.connection::sendPacket);

        chunkDataPacketsFor(this.connection.getMinecraftVersion())
                .forEach(this.connection::sendPacket);

    }

    protected static final UUID EMPTY = new UUID(0, 0);

}
