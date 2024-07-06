package pl.mrstudios.proxy.logger;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.logger.stream.LoggerFilteringStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.*;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static java.nio.file.Files.*;
import static java.time.Instant.now;
import static java.time.ZoneId.systemDefault;
import static pl.mrstudios.proxy.logger.Logger.Level.*;

public class Logger implements Thread.UncaughtExceptionHandler {

    private final FileWriter fileWriter;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(systemDefault());

    public Logger() {

        /* Modify Output Stream*/
        setOut(filteringLoggerStream);
        setErr(filteringLoggerStream);

        /* Exception Handler */
        setDefaultUncaughtExceptionHandler(this);

        try {

            File logDirectory = new File("logs"),
                    logFile = new File(logDirectory, "latest.log");

            if (!logDirectory.exists())
                if (!logDirectory.mkdirs())
                    throw new RuntimeException("Unable to create 'logs' directory.");

            createDirectories(logDirectory.toPath());

            if (logFile.exists())
                move(
                        logFile.toPath(),
                        new File(logDirectory, format(
                                "%s.log.bck", DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss")
                                        .withZone(systemDefault()).format(fileCreationTime(logFile))
                        )).toPath()
                );

            if (!logFile.exists())
                if (!logFile.createNewFile())
                    throw new RuntimeException("Unable to create 'latest.log' file.");

            setAttribute(logFile.toPath(), "creationTime", FileTime.from(now()));
            this.fileWriter = new FileWriter(logFile);

        } catch (Exception exception) {
            throw new RuntimeException("Unable to initialize logger due to exception.", exception);
        }

    }

    public void info(@NotNull String message) { log(INFO, message); }
    public void error(@NotNull String message) { log(ERROR, message); }
    public void warn(@NotNull String message) { log(WARNING, message); }

    public void info(@NotNull String message, @Nullable Object... args) { log(INFO, format(message, args)); }
    public void error(@NotNull String message, @Nullable Object... args) { log(ERROR, format(message, args)); }
    public void warn(@NotNull String message, @Nullable Object... args) { log(WARNING, format(message, args));}

    public void log(@NotNull Level level, @NotNull String message) {

        System.out.printf("[%s] [%s%s\u001B[0m] %s%n", this.dateTimeFormatter.format(now()), level.prefix, level, message);

        try {

            this.fileWriter.write(format(
                    "[%s] [%s] %s%s", this.dateTimeFormatter.format(now()), level, message, lineSeparator()
            )); this.fileWriter.flush();

        } catch (Exception ignored) {}

    }

    @Override
    @SuppressWarnings("all")
    public void uncaughtException(Thread thread, Throwable throwable) {
        this.error("Exception in thread %s happened. (%s)", thread.getName(), throwable.getClass().getSimpleName());
        throwable.printStackTrace();
    }

    @AllArgsConstructor
    public enum Level {

        INFO("\u001B[34m"),
        ERROR("\u001B[31m"),
        WARNING("\u001B[33m");

        private final String prefix;

    }

    protected static @NotNull Instant fileCreationTime(@NotNull File file) throws IOException {
        return ((FileTime) getAttribute(file.toPath(), "creationTime")).toInstant();
    }

    protected static final PrintStream filteringLoggerStream = new PrintStream(new LoggerFilteringStream(out, List.of(
            ",login", ",register"
    )));

}
