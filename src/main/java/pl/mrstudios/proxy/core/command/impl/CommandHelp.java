package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.commons.reflection.Reflections;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;

import java.util.*;

import static com.google.common.collect.Lists.partition;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.rangeClosed;
import static pl.mrstudios.proxy.core.command.CommandDescription.Parameter;
import static pl.mrstudios.proxy.core.language.Language.LanguageType;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Command(name = "help")
@CommandDescription(
        name = "help",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Display list of available commands."),
                @LanguageEntry(key = POLISH, value = "Wyświetla listę dostępnych komend."),
                @LanguageEntry(key = RUSSIAN, value = "Отображает список доступных команд.")
        },
        parameters = {
                @Parameter(
                        name = "page",
                        description = {
                                @LanguageEntry(key = ENGLISH, value = "Number of help page."),
                                @LanguageEntry(key = POLISH, value = "Numer strony pomocy."),
                                @LanguageEntry(key = RUSSIAN, value = "Номер страницы помощи.")
                        }
                )
        }
)
public class CommandHelp {

    private final Map<LanguageType, Collection<String>> commands;
    private final Map<LanguageType, Map<Integer, Collection<String>>> pages;

    @Inject
    public CommandHelp() {

        this.pages = new EnumMap<>(LanguageType.class);
        this.commands = new EnumMap<>(LanguageType.class);
        stream(values())
                .forEach((value) -> this.commands.put(value, new ArrayList<>()));

        new Reflections<>("pl.mrstudios.proxy")
                .getClassesAnnotatedWith(CommandDescription.class)
                .stream().map((clazz) -> clazz.getAnnotation(CommandDescription.class))
                .forEach(
                        (commandDescription) -> this.commands.keySet()
                                .forEach((language) -> {

                                    StringBuilder parameters = new StringBuilder();
                                    stream(commandDescription.parameters())
                                            .forEach((parameter) -> parameters.append(format(
                                                    language.getLanguage().commandHelpEntryParameter, parameter.name(), translationFor(language, parameter.description())
                                            )));

                                    this.commands.get(language)
                                            .add(format(
                                                    language.getLanguage().commandHelpEntry,
                                                    format(
                                                            "%s %s", commandDescription.name(),
                                                            (commandDescription.parameters().length > 0) ?
                                                                    join(" ", stream(commandDescription.parameters())
                                                                            .map((parameter) -> format("<%s>", parameter.name()))
                                                                            .toList()
                                                                    ) : ""
                                                    ),
                                                    (translationFor(language, commandDescription.description()).isBlank()) ?
                                                            language.getLanguage().commandHelpEntryHoverNoDescription :
                                                            format(language.getLanguage().commandHelpEntryHoverDescription,
                                                                    format("%s %s",
                                                                            commandDescription.name(),
                                                                            join(" ", stream(commandDescription.parameters())
                                                                                    .map((parameter) -> format("<%s>", parameter.name()))
                                                                                    .toList()
                                                                            )
                                                                    ),
                                                                    parameters.isEmpty() ? "" : format(language.getLanguage().commandHelpSectionParameters, parameters)
                                                            ),
                                                    commandDescription.name(), translationFor(language, commandDescription.description())
                                            ));

                                })
                );

        this.commands.keySet()
                .stream().peek((language) -> this.pages.put(language, new HashMap<>()))
                .forEach((language) -> {

                    List<List<String>> pages = partition(new ArrayList<>(this.commands.get(language)), 10);
                    rangeClosed(1, pages.size())
                            .forEach((i) -> this.pages.get(language).put(i, pages.get(i - 1)));

                });

        this.commands.clear();

    }

    @Execute
    public void execute(@Context @HasGroup(USER) User user, @Arg("page") Optional<Integer> optionalPage) {

        int page = (this.pages.get(user.getLanguage().type()).containsKey(optionalPage.orElse(1))) ? optionalPage.orElse(1) : 1;

        user.sendMessage(user.getLanguage().commandHelpHeader, page, this.pages.get(user.getLanguage().type()).size());
        this.pages.get(user.getLanguage().type())
                .getOrDefault(page, this.pages.get(user.getLanguage().type()).get(1))
                .forEach(user::sendMessage);
        user.sendMessage(user.getLanguage().commandHelpFooter, page - 1, page + 1);

    }

    protected static @NotNull String translationFor(@NotNull LanguageType languageType, @NotNull LanguageEntry[] key) {
        return stream(key)
                .filter((entry) -> entry.key() == languageType).findFirst()
                .map(LanguageEntry::value).orElse("");
    }

}
