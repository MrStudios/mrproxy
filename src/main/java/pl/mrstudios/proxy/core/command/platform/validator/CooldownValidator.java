package pl.mrstudios.proxy.core.command.platform.validator;

import com.google.common.cache.Cache;
import dev.rollczi.litecommands.annotations.validator.requirment.AnnotatedValidator;
import dev.rollczi.litecommands.command.executor.CommandExecutor;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.requirement.Requirement;
import dev.rollczi.litecommands.validator.ValidatorResult;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.user.User;

import java.time.Duration;
import java.time.Instant;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static dev.rollczi.litecommands.validator.ValidatorResult.invalid;
import static dev.rollczi.litecommands.validator.ValidatorResult.valid;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

public class CooldownValidator implements AnnotatedValidator<User, User, Cooldown> {

    private final Cache<String, Instant> cooldowns = newBuilder()
            .expireAfterWrite(60, SECONDS)
            .build();

    @Override
    @SneakyThrows
    public @NotNull ValidatorResult validate(
            @NotNull Invocation<User> invocation,
            @NotNull CommandExecutor<User> commandExecutor,
            @NotNull Requirement<User> requirement,
            @NotNull User user,
            @NotNull Cooldown annotation
    ) {

        this.cooldowns.get(invocation.sender().getName(), Instant::now);

        if (requireNonNull(this.cooldowns.getIfPresent(invocation.sender().getName())).isAfter(now()))
            return invalid(format(
                    user.getLanguage().errorMustWaitBeforeNextUsage, Duration.between(now(), this.cooldowns.getIfPresent(invocation.sender().getName())).toMillis()
            ));

        this.cooldowns.put(invocation.sender().getName(), now().plusMillis(annotation.value()));
        return valid();

    }

}
