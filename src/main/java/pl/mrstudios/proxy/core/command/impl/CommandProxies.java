package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.service.impl.proxy.ProxyService;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyCountry;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;
import pl.mrstudios.proxy.core.service.impl.proxy.enums.ProxyType;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.enums.Group;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.service.impl.proxy.enums.ProxyType.values;

@Command(
        name = "proxies",
        aliases = {
                "proxylist"
        }
)
@CommandDescription(
        name = "proxies",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Display proxies information and amount."),
                @LanguageEntry(key = POLISH, value = "Wyświetl informacje o proxy i ich ilość."),
                @LanguageEntry(key = RUSSIAN, value = "Отобразить информацию о прокси и их количество.")
        }
)
public class CommandProxies {

    private final ProxyService proxyService;

    @Inject
    public CommandProxies(
            @NotNull ProxyService proxyService
    ) {
        this.proxyService = proxyService;
    }

    @Execute
    public void execute(
            @Context @HasGroup(Group.USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(user.getLanguage().commandProxiesListHeader);
        stream(values())
                .filter(ProxyType::isVisible)
                .forEach((proxyType) -> {

                    StringBuilder hover = new StringBuilder();
                    Map<ProxyCountry, Integer> countries = new EnumMap<>(ProxyCountry.class);
                    Collection<ProxyEntry> proxy = this.proxyService.getProxies(proxyType);

                    proxy.forEach((proxyEntry) -> countries.put(
                            proxyEntry.country(), countries.computeIfAbsent(proxyEntry.country(), (key) -> 0) + 1
                    ));
                    hover.append(format(
                            user.getLanguage().commandProxiesListEntryHover,
                            countries.keySet()
                                    .stream()
                                    .map((country) -> format(user.getLanguage().commandProxiesListEntryHoverCountryEntry, country.name(), countries.get(country)))
                                    .collect(joining())
                            )
                    );

                    stringBuilder.append(format(user.getLanguage().commandProxiesListEntry, hover, proxyType.name(), proxy.size(), this.proxyService.averageLatency(proxyType)));

                });

        stringBuilder.append("<br>").append(user.getLanguage().commandProxiesListFooter);
        user.sendMessage(stringBuilder.toString());

    }

}
