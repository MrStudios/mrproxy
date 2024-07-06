package pl.mrstudios.proxy.core.command.platform.annotations;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.user.enums.Group;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface HasGroup {
    @NotNull Group value();
}
