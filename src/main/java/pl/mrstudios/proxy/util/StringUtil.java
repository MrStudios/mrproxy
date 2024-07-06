package pl.mrstudios.proxy.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.core.language.Language;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;

public class StringUtil {

    public static @NotNull String throwableToString(@NotNull Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter, true));
        return stringWriter.getBuffer().toString();
    }

    public static @NotNull String applyLegacyColors(@NotNull String input, @Nullable Object... args) {
        return format(input, args).replace("&", "§");
    }

    public static @Nullable String readImage(@NotNull String file) {

        try {

            BufferedImage bufferedImage = read(new File(file));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            write(bufferedImage, "png", Base64.getEncoder().wrap(byteArrayOutputStream));

            return "data:image/png;base64," + byteArrayOutputStream.toString(ISO_8859_1);

        } catch (Exception ignored) {}

        return null;

    }

    public static @NotNull String expiresFormatHighest(@NotNull Language language, @NotNull Duration duration) {

        StringBuilder stringBuilder = new StringBuilder();

        if (duration.toDays() > 0)
            stringBuilder.append(duration.toDays())
                    .append(" ")
                    .append(plural(duration.toDays(), language.pluralOneDay, language.pluralTwoDays, language.pluralFiveDays));

        else if (duration.toHours() > 0)
            stringBuilder.append(duration.toHours())
                    .append(" ")
                    .append(plural(duration.toHours(), language.pluralOneHour, language.pluralTwoHours, language.pluralFiveHours));

        else if (duration.toMinutes() > 0)
            stringBuilder.append(duration.toMinutes())
                    .append(" ")
                    .append(plural(duration.toMinutes(), language.pluralOneMinute, language.pluralTwoMinutes, language.pluralFiveMinutes));

        else if (duration.toSeconds() > 0)
            stringBuilder.append(duration.toSeconds())
                    .append(" ")
                    .append(plural(duration.toSeconds(), language.pluralOneSecond, language.pluralTwoSeconds, language.pluralFiveSeconds));

        return stringBuilder.toString();

    }

    public static @NotNull String plural(@NotNull Long amount, @NotNull String one, @NotNull String two, @NotNull String five) {
        return amount == 1 ? one : (amount >= 5) ? five : two;
    }

    public static @NotNull String removeLegacyColors(@NotNull String string) {
        return pattern.matcher(string).replaceAll("");
    }

    protected static final Pattern pattern = compile("§[a-fklmnor0-9]", CASE_INSENSITIVE);

}
