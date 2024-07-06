package pl.mrstudios.proxy.util;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.language.Language;

import static pl.mrstudios.proxy.core.language.Language.LanguageType.ENGLISH;

public class ReflectUtil {

    public static void callVoid() {}
    public static Object defaultValueOf(@NotNull Class<?> objectClass) {

        if (objectClass == boolean.class || objectClass == Boolean.class)
            return false;

        if (objectClass == int.class || objectClass == Integer.class)
            return 0;

        if (objectClass == long.class || objectClass == Long.class)
            return 0L;

        if (objectClass == float.class || objectClass == Float.class)
            return 0.0f;

        if (objectClass == double.class || objectClass == Double.class)
            return 0.0d;

        if (objectClass == String.class)
            return "";

        if (objectClass == Language.LanguageType.class)
            return ENGLISH;

        throw new UnsupportedOperationException("Unsupported object type. (" + objectClass.getSimpleName() + ")");

    }

}
