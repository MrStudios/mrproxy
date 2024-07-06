package pl.mrstudios.proxy.core.command.platform.validator;

import dev.rollczi.litecommands.annotations.validator.requirment.AnnotatedValidator;
import dev.rollczi.litecommands.command.executor.CommandExecutor;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.requirement.Requirement;
import dev.rollczi.litecommands.validator.ValidatorResult;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.user.User;

import static dev.rollczi.litecommands.validator.ValidatorResult.invalid;
import static dev.rollczi.litecommands.validator.ValidatorResult.valid;

public class HasGroupValidator implements AnnotatedValidator<User, User, HasGroup> {

    @Override
    public @NotNull ValidatorResult validate(
            @NotNull Invocation<User> invocation,
            @NotNull CommandExecutor<User> commandExecutor,
            @NotNull Requirement<User> requirement,
            @NotNull User user,
            @NotNull HasGroup annotation
    ) {

        if (user.getAccount().getGroup().getPermissionLevel() < annotation.value().getPermissionLevel())
            return invalid(user.getLanguage().errorNoPermissions);

        return valid();
    }

}
