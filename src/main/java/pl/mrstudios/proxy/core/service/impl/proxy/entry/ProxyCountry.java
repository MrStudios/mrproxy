package pl.mrstudios.proxy.core.service.impl.proxy.entry;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static java.util.Arrays.stream;

@AllArgsConstructor
public enum ProxyCountry {

    UNKNOWN("Unknown"),
    USA("United States"),
    AUSTRIA("Austria"),
    FRANCE("France"),
    GERMANY("Germany"),
    GREECE("Greece"),
    HUNGARY("Hungary"),
    ITALY("Italy"),
    JAPAN("Japan"),
    POLAND("Poland"),
    ROMANIA("Romania"),
    SPAIN("Spain"),
    TURKEY("Turkey"),
    UNITED_KINGDOM("United Kingdom");

    private final String country;

    public static @NotNull ProxyCountry of(@NotNull String string) {
        return stream(values())
                .filter((country) -> country.country().equalsIgnoreCase(string))
                .findFirst().orElse(UNKNOWN);
    }

    public @NotNull String country() {
        return this.country;
    }

}
