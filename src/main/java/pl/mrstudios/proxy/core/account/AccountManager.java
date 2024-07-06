package pl.mrstudios.proxy.core.account;

import com.google.common.cache.Cache;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mrstudios.proxy.core.mysql.MySQL;
import pl.mrstudios.proxy.core.mysql.sql.result.SQLResult;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.sql.Timestamp.from;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MINUTES;
import static pl.mrstudios.proxy.core.account.AccountSqlRepository.*;
import static pl.mrstudios.proxy.core.user.enums.Group.valueOf;

public class AccountManager {

    private final MySQL mySQL;
    private final Cache<String, Account> cache;

    public AccountManager(MySQL mySQL) {

        this.mySQL = mySQL;
        this.cache = newBuilder()
                .expireAfterWrite(1, MINUTES)
                .build();

        this.mySQL.execute(this.mySQL.make(createTable));

    }

    @SneakyThrows
    public @Nullable Account fetch(@NotNull String name) {
        return ofNullable(this.cache.getIfPresent(name))
                .orElseGet(() -> {

                    SQLResult result = this.mySQL.fetch(
                                    this.mySQL.make(selectAccount)
                                            .setString(1, name)
                            )
                            .stream()
                            .findFirst()
                            .orElse(null);

                    Account account = ofNullable(result)
                            .map((entry) -> new Account(
                                    entry.entry("name").asString(),
                                    valueOf(entry.entry("group").asString()),
                                    entry.entry("expires").asInstant(),
                                    Account.Data.deserialize(entry.entry("data").asString()),
                                    Account.Settings.deserialize(entry.entry("settings").asString())
                            ))
                            .orElse(null);

                    ofNullable(account)
                            .ifPresent((object) -> this.cache.put(object.getName(), object));

                    return account;

                });
    }

    public void save(@NotNull Account account) {
        this.cache.invalidate(account.getName());
        this.mySQL.execute(
                this.mySQL.make(updateAccount)
                        .setString(1, account.getGroup().name())
                        .setTimestamp(2, from(account.getExpires()))
                        .setLongString(3, Account.Data.serialize(account.getData()))
                        .setLongString(4, Account.Settings.serialize(account.getSettings()))
                        .setString(5, account.getName())
        );
    }

    public void create(@NotNull Account account) {
        this.cache.invalidate(account.getName());
        this.mySQL.execute(
                this.mySQL.make(insertAccount)
                        .setString(1, account.getName())
                        .setString(2, account.getGroup().name())
                        .setTimestamp(3, from(account.getExpires()))
                        .setLongString(4, Account.Data.serialize(account.getData()))
                        .setLongString(5, Account.Settings.serialize(account.getSettings()))
        );
    }

}
