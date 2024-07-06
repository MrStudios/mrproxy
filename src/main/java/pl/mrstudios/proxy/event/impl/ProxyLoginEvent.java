package pl.mrstudios.proxy.event.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.event.Event;
import pl.mrstudios.proxy.event.interfaces.Cancellable;
import pl.mrstudios.proxy.netty.connection.Connection;

@Getter @Setter
public class ProxyLoginEvent implements Event, Cancellable {

    private String reason;
    private final Connection connection;

    private boolean cancelled;

    public ProxyLoginEvent(@NotNull Connection connection) {
        this.connection = connection;
    }

    public void disallowed(@NotNull String reason) {
        this.reason = reason;
        this.cancelled = true;
    }

}
