package pl.mrstudios.proxy.core.language;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static pl.mrstudios.proxy.core.language.Language.LanguageType;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface LanguageEntry {
    @NotNull LanguageType key();
    @NotNull String value();
}
