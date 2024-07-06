package pl.mrstudios.proxy.core.command.platform.sender;

import dev.rollczi.litecommands.identifier.Identifier;
import dev.rollczi.litecommands.platform.AbstractPlatformSender;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.user.User;

import static dev.rollczi.litecommands.identifier.Identifier.of;

public class ProxySender extends AbstractPlatformSender {

    private final User user;

    public ProxySender(@NotNull User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return this.user.getName();
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return of(this.user.getConnection().getGameProfile().getId());
    }

    @Override
    @SuppressWarnings("all")
    public boolean hasPermission(@NotNull String s) {
        return true;
    }

}
