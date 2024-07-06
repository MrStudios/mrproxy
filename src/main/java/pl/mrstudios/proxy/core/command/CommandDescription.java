package pl.mrstudios.proxy.core.command;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.language.LanguageEntry;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface CommandDescription {

    @NotNull String name();
    @NotNull Parameter[] parameters() default {};
    @NotNull LanguageEntry[] description() default {};

    @interface Parameter {
        @NotNull String name();
        @NotNull LanguageEntry[] description() default {};
    }

}
