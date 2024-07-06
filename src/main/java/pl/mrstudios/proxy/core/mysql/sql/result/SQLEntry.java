package pl.mrstudios.proxy.core.mysql.sql.result;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.Instant;

public record SQLEntry(
        @NotNull String key,
        @NotNull Class<?> type,
        @NotNull Object object
) {

    public @NotNull String asString() {
        return (String) this.object;
    }

    public @NotNull Integer asInteger() {
        return (Integer) this.object;
    }

    public @NotNull Instant asInstant() {
        return ((Timestamp) this.object).toInstant();
    }

}
