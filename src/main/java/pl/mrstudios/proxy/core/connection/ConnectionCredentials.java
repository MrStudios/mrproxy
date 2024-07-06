package pl.mrstudios.proxy.core.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

import static java.lang.String.format;

public record ConnectionCredentials(
        @NotNull String host,
        @Nullable Integer port
) {

    public @NotNull ConnectionCredentials get() {
        return (this.port == null || this.port <= 0) ?
                resolve(this) : this;
    }

    private static @NotNull ConnectionCredentials resolve(@NotNull ConnectionCredentials connectionCredentials) {

        try {

            Hashtable<Object, Object> hashtable = new Hashtable<>();

            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");

            InitialDirContext initialDirContext = new InitialDirContext(hashtable);
            Attributes attributes = initialDirContext.getAttributes(
                    format("_minecraft._tcp.%s", connectionCredentials.host),
                    new String[] {
                            "SRV"
                    }
            );

            String[] strings = attributes.get("srv").get().toString().split(" ", 4);

            String host = strings[3];
            if (strings[3].endsWith("\\."))
                host = strings[3].substring(0, (strings[3].length() - 1));

            return new ConnectionCredentials(host, Integer.parseInt(strings[2]));

        } catch (Exception exception) {
            return new ConnectionCredentials(connectionCredentials.host, 25565);
        }

    }

    @Override
    public String toString() {
        return format("%s:%d", this.host, this.port);
    }

}
