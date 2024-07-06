package pl.mrstudios.proxy.core.listener;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.account.Account;
import pl.mrstudios.proxy.core.account.AccountManager;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.ProxyLoginEvent;
import pl.mrstudios.proxy.event.impl.ProxyUserJoinedEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;
import pl.mrstudios.proxy.netty.packet.impl.login.server.ServerLoginSuccessPacket;

import static java.lang.String.join;
import static java.time.Instant.now;
import static java.util.List.of;
import static pl.mrstudios.proxy.netty.enums.ConnectionState.PLAY;

public class ProxyConnectionLoginListener implements Listener {

    private final UserManager userManager;
    private final EventManager eventManager;
    private final AccountManager accountManager;

    @Inject
    public ProxyConnectionLoginListener(
            @NotNull UserManager userManager,
            @NotNull EventManager eventManager,
            @NotNull AccountManager accountManager
    ) {
        this.userManager = userManager;
        this.eventManager = eventManager;
        this.accountManager = accountManager;
    }

    @EventHandler
    public void onProxyLogin(@NotNull ProxyLoginEvent event) {

        Account account = this.accountManager.fetch(event.getConnection().getGameProfile().getName());

        if (account == null) {
            event.disallowed(join("<br>", of(
                    "You don't have access to proxy.",
                    "You can purchase access at <aqua>www.mrproxy.net</aqua>"
            )));
            return;
        }

        if (now().isAfter(account.getExpires()))
            event.disallowed(join("<br>", of(
                    "Your access to proxy has expired.",
                    "You can extend access at <aqua>www.mrproxy.net</aqua>"
            )));

        if (this.userManager.users().stream().anyMatch((entry) -> entry.getAccount().getName().equalsIgnoreCase(account.getName())))
            event.disallowed(join("<br>", "This account is already connected to proxy."));

        if (event.isCancelled())
            return;

        User user = new User(account, event.getConnection());

        user.getConnection().setCompressionThreshold(256);
        user.getConnection().sendPacket(new ServerLoginSuccessPacket(user.getConnection().getGameProfile()));
        user.getConnection().setConnectionState(PLAY);

        this.userManager.users().add(user);
        this.eventManager.call(new ProxyUserJoinedEvent(user));

    }

}
