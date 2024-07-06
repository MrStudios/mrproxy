package pl.mrstudios.proxy.core.command.platform.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Range {
    int min() default MIN_VALUE;
    int max() default MAX_VALUE;
}
