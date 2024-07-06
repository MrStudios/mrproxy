package pl.mrstudios.proxy.core.account;

import com.google.gson.Gson;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.user.enums.Group;

import java.time.Duration;
import java.time.Instant;

import static java.time.Instant.now;
import static pl.mrstudios.proxy.core.language.Language.LanguageType;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.ENGLISH;

@ToString
@Getter @Setter
@AllArgsConstructor
public class Account {

    private String name;
    private Group group;
    private Instant expires;

    private Data data;
    private Settings settings;

    @ToString
    @Builder(builderClassName = "Builder")
    public static class Data {

        public Integer coins;
        public String password;
        public String internetProvider;

        public static @NotNull String serialize(@NotNull Data data) {
            return gson.toJson(data);
        }

        public static @NotNull Data deserialize(@NotNull String string) {
            return gson.fromJson(string, Data.class);
        }

    }

    @ToString
    @Builder(builderClassName = "Builder")
    public static class Settings {

        public LanguageType language;

        public Integer macroDelay;
        public Integer motherDelay;
        public Boolean incognito;
        public Boolean displayBotChat;
        public Boolean displayBotInfo;
        public Boolean blockResourcePacks;

        public Boolean autoRegisterBots;
        public Boolean autoRegisterPlayer;
        public String autoRegisterBotsPassword;
        public String autoRegisterPlayerPassword;

        public Boolean autoReconnectPlayer;
        public Integer autoReconnectPlayerDelay;

        public Boolean displayLastPacketReceived;

        public static @NotNull String serialize(@NotNull Settings settings) {
            return gson.toJson(settings);
        }

        public static @NotNull Settings deserialize(@NotNull String string) {
            return gson.fromJson(string, Settings.class);
        }

    }

    public static @NotNull Account create(@NotNull String name, @NotNull Group group, @NotNull Duration validity) {
        return new Account(
                name,
                group,
                now().plus(validity),
                Data.builder()
                        .coins(0)
                        .internetProvider("")
                        .password("")
                        .build(),
                Settings.builder()
                        .language(ENGLISH)
                        .macroDelay(0)
                        .motherDelay(0)
                        .incognito(false)
                        .displayBotChat(false)
                        .displayBotInfo(false)
                        .autoRegisterBots(false)
                        .autoRegisterPlayer(false)
                        .blockResourcePacks(false)
                        .autoReconnectPlayer(false)
                        .autoReconnectPlayerDelay(1000)
                        .autoRegisterBotsPassword("ZAQ!2wsx")
                        .autoRegisterPlayerPassword("ZAQ!2wsx")
                        .displayLastPacketReceived(false)
                        .build()
        );
    }

    protected static final Gson gson = new Gson();

}
