package pl.mrstudios.proxy.core.command.platform.validator;

import dev.rollczi.litecommands.annotations.validator.requirment.AnnotatedValidator;
import dev.rollczi.litecommands.command.executor.CommandExecutor;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.requirement.Requirement;
import dev.rollczi.litecommands.validator.ValidatorResult;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.command.platform.annotations.Range;
import pl.mrstudios.proxy.core.user.User;

import static dev.rollczi.litecommands.validator.ValidatorResult.invalid;
import static dev.rollczi.litecommands.validator.ValidatorResult.valid;
import static java.lang.String.format;

public class RangeValidator implements AnnotatedValidator<User, Integer, Range> {

    @Override
    public @NotNull ValidatorResult validate(
            @NotNull Invocation<User> invocation,
            @NotNull CommandExecutor<User> commandExecutor,
            @NotNull Requirement<Integer> requirement,
            @NotNull Integer integer,
            @NotNull Range annotation
    ) {

        if (integer < annotation.min() || integer > annotation.max())
            return invalid(format(invocation.sender().getLanguage().errorNotInRange, requirement.getName()));

        return valid();

    }

}
