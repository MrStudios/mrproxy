package pl.mrstudios.proxy.core.listener;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.account.AccountManager;
import pl.mrstudios.proxy.core.connection.RemoteConnection;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.core.user.entity.Bot;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.ProxyUserDisconnectedEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;

import static java.lang.System.gc;
import static java.util.Optional.ofNullable;

public class ProxyUserDisconnectListener implements Listener {

    private final UserManager userManager;
    private final AccountManager accountManager;

    @Inject
    public ProxyUserDisconnectListener(
            @NotNull UserManager userManager,
            @NotNull AccountManager accountManager
    ) {
        this.userManager = userManager;
        this.accountManager = accountManager;
    }

    @EventHandler
    public void onUserDisconnect(@NotNull ProxyUserDisconnectedEvent event) {

        this.accountManager.save(event.user().getAccount());
        this.userManager.users().removeIf(
                (user) -> user.getAccount().getName().equalsIgnoreCase(event.user().getAccount().getName())
        );

        ofNullable(event.user().getScheduledFuture())
                .ifPresent((future) -> future.cancel(true));

        ofNullable(event.user().getRemoteConnection())
                .ifPresent((connection) -> connection.disconnect(null, true));

        if (!event.user().isConnected())
            return;

        event.user().setConnected(false);
        this.userManager.users().forEach(
                (target) -> target.sendMessage(target.getLanguage().proxyQuitMessageFormat, event.user().getConnection().getMinecraftVersion().getName(), event.user().getAccount().getGroup().name(), event.user().getAccount().getName())
        );

        event.user().getBots()
                .stream().map(Bot::connection)
                .forEach(RemoteConnection::disconnect);

        event.user().getBots().clear();
        gc();

    }

}
