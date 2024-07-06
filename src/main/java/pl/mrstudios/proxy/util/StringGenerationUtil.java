package pl.mrstudios.proxy.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.core.Application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.RandomStringUtils.random;

public class StringGenerationUtil {

    public static @NotNull String generateString(
            @NotNull String string,
            @NotNull Integer maxLength,
            @Nullable Integer number
    ) {

        String nameGeneration = string;
        Matcher matcher = NAME_GENERATOR_PATTERN.matcher(string);

        while (matcher.find()) try {

            String key = matcher.group(1);
            int length = parseInt(matcher.group(2));

            switch (key.toLowerCase()) {

                case "n" ->
                        nameGeneration = nameGeneration.replace(format("[%s:%d]", key, length), random(length, false, true));

                case "s" ->
                        nameGeneration = nameGeneration.replace(format("[%s:%d]", key, length), random(length, true, false));

                case "r" ->
                        nameGeneration = nameGeneration.replace(format("[%s:%d]", key, length), random(length, true, true));

            }

        } catch (Exception ignored) {}

        if (string.equalsIgnoreCase("[real]"))
            nameGeneration = format("%s%s%s", randomElementOf(NAME_KEY_WORDS), randomElementOf(NAME_KEY_WORDS), current().nextBoolean() ? random(4, false, true) : "");

        if (nameGeneration.equals(string) && number != null)
            nameGeneration = format("%s%d", nameGeneration, number);

        return nameGeneration.substring(0, min(nameGeneration.length(), maxLength));

    }

    protected static final Pattern NAME_GENERATOR_PATTERN = compile("\\[([nsr]):(\\d+)]", CASE_INSENSITIVE);
    protected static final String[] NAME_KEY_WORDS;

    protected static @NotNull <T> T randomElementOf(T[] array) {
        return array[current().nextInt(0, array.length)];
    }

    static {

        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requireNonNull(Application.class.getClassLoader().getResourceAsStream("data/words.txt"))))
        ) {
            NAME_KEY_WORDS = bufferedReader.lines().toArray(String[]::new);
        } catch (Exception exception) {
            throw new RuntimeException("Unable to load name key words.", exception);
        }

    }

}
