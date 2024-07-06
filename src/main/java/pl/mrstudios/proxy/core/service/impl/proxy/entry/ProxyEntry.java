package pl.mrstudios.proxy.core.service.impl.proxy.entry;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.service.impl.proxy.enums.ProxyType;

import java.net.Proxy;

public record ProxyEntry(
        @NotNull Proxy proxy,
        @NotNull ProxyType type,
        @NotNull ProxyCountry country,
        @NotNull Integer latency
) {}
