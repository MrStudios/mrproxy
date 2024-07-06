package pl.mrstudios.proxy.core.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class UserManager {

    private final Collection<User> users;

    public UserManager() {
        this.users = new ArrayList<>();
    }

    public @Nullable User user(@NotNull String name) {
        return this.users.stream()
                .filter((user) -> user.getAccount().getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public @NotNull Collection<User> users() {
        return this.users;
    }

}
