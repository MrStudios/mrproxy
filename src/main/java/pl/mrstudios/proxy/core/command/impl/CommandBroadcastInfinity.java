package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import lombok.SneakyThrows;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.command.platform.annotations.Range;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.netty.packet.impl.play.client.ClientChatMessagePacket;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.time.Instant.now;
import static java.util.BitSet.valueOf;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;
import static pl.mrstudios.proxy.util.StringGenerationUtil.generateString;

@Command(
        name = "bcinfinity",
        aliases = { "broadcastinfinity" }
)
@CommandDescription(
        name = "bcinfinity",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Send message through bots."),
                @LanguageEntry(key = POLISH, value = "Wyślij wiadomość poprzez boty."),
                @LanguageEntry(key = RUSSIAN, value = "Отправить сообщение через ботов.")
        },
        parameters = {
                @Parameter(
                        name = "delay/stop",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Delay between messages."),
                                @LanguageEntry(key = POLISH, value = "Opóźnienie między wiadomościami."),
                                @LanguageEntry(key = RUSSIAN, value = "Задержка между сообщениями.")
                        }
                ),
                @Parameter(
                        name = "message",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Message which will be sent by bots."),
                                @LanguageEntry(key = POLISH, value = "Wiadomość która zostanie wysłana przez boty."),
                                @LanguageEntry(key = RUSSIAN, value = "Сообщение, которое будет отправлено ботами.")
                        }
                )
        }
)
public class CommandBroadcastInfinity {

    @Inject
    public CommandBroadcastInfinity() {}

    @Execute
    @SneakyThrows
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user,
            @Arg("delay") @Range(min = 300, max = 10000) int delay,
            @Join("message") String message
    ) {

        if (user.getRemoteConnection() == null) {
            user.sendMessage(user.getLanguage().errorYouMustBeConnected);
            return;
        }

        if (user.getBots().isEmpty()) {
            user.sendMessage(user.getLanguage().errorNoConnectedBots);
            return;
        }

        if (user.getScheduledFuture() != null) {
            user.getScheduledFuture().cancel(true);
            user.setScheduledFuture(null);
        }

        user.setScheduledFuture(
                executorService.scheduleAtFixedRate(
                        () -> user.getBots().stream().filter(Objects::nonNull)
                                .filter((bot) -> bot.connection().isConnected())
                                .forEach((bot) -> bot.connection().sendPacket(new ClientChatMessagePacket(
                                        generateString(message, 256, null), now().toEpochMilli(), 0, null, 0, valueOf(new long[0])
                                ))),
                        0, delay, MILLISECONDS
                )
        );

        user.sendMessage(user.getLanguage().commandBroadcastInfinityMessageScheduled, message);

    }

    @Execute(name = "stop")
    public void executeStop(
            @Context @HasGroup(USER) User user
    ) {

        Optional.ofNullable(user.getScheduledFuture())
                .ifPresent((future) -> future.cancel(true));
        user.setScheduledFuture(null);

        user.sendMessage(user.getLanguage().commandBroadcastInfinityStopped);

    }

    protected static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

}
