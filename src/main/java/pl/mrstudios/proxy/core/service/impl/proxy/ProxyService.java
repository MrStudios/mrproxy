package pl.mrstudios.proxy.core.service.impl.proxy;

import kong.unirest.Unirest;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Pair;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.config.Configuration;
import pl.mrstudios.proxy.core.service.Service;
import pl.mrstudios.proxy.core.service.impl.proxy.checker.ProxyChecker;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyCountry;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;
import pl.mrstudios.proxy.core.service.impl.proxy.enums.ProxyType;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.core.user.enums.Group;
import pl.mrstudios.proxy.logger.Logger;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.lang.Integer.parseInt;
import static java.math.RoundingMode.HALF_UP;
import static java.net.Proxy.NO_PROXY;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.stream;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyCountry.UNKNOWN;
import static pl.mrstudios.proxy.core.service.impl.proxy.enums.ProxyType.valueOf;

@SuppressWarnings("all")
public class ProxyService implements Service {

    private ProxyChecker proxyChecker;
    private ScheduledFuture<?> scheduledFuture;
    private final ScheduledExecutorService executorService;

    private final Logger logger;
    private final MiniMessage miniMessage;
    private final UserManager userManager;
    private final Configuration configuration;
    private final Map<ProxyType, List<ProxyEntry>> proxies;

    @Inject
    public ProxyService(
            @NotNull Logger logger,
            @NotNull MiniMessage miniMessage,
            @NotNull UserManager userManager,
            @NotNull Configuration configuration
    ) {

        this.logger = logger;
        this.miniMessage = miniMessage;
        this.userManager = userManager;
        this.configuration = configuration;
        this.proxies = new ConcurrentHashMap<>();
        this.executorService = newSingleThreadScheduledExecutor();
        stream(ProxyType.values())
                .forEach((type) -> this.proxies.put(type, new ArrayList<>()));

    }

    @Override
    public void run() {

        this.proxies.values().forEach(Collection::clear);
        this.logger.info("Started checking proxies, please wait..");

        List<ProxyEntry> proxies = new ArrayList<>();
        this.configuration.general.proxyGrabberUrls
                .forEach((key, value) -> value.forEach((type, list) -> list.forEach(
                        (url) -> fetchProxies(type, url).stream().map((proxy) -> new ProxyEntry(proxy, key, UNKNOWN, 0))
                                .forEach(proxies::add)
                )));

        this.proxyChecker = new ProxyChecker(
                proxies, 48, 5000,
                (proxy) -> this.proxies.computeIfAbsent(proxy.type(), (key) -> new ArrayList<>()).add(proxy)
        );

        this.proxyChecker.start();
        this.scheduledFuture = this.executorService.scheduleAtFixedRate(() -> {

            if (this.proxyChecker.isDone()) {
                this.logger.info("Finished checking proxies, found %d working proxies. (%dms/avg)", this.proxyChecker.working().size(), this.proxyChecker.avgLatency());
                this.proxyChecker.executorService().shutdown();
                this.scheduledFuture.cancel(true);
                return;
            }

            this.userManager.users()
                    .stream().filter((user) -> user.getRemoteConnection() == null)
                    .forEach(
                            (user) -> user.sendActionBar(
                                    user.getLanguage().serviceProxyCheckingNotify,
                                    String.format("%.2f", round(this.proxyChecker.progress(), 2)).replace(".", ",") + "%",
                                    this.proxyChecker.working().size()
                            )
                    );
        }, 0, 500, MILLISECONDS);

    }

    @Override
    public Duration repeatDelay() {
        return Duration.of(90, MINUTES);
    }

    public @NotNull Collection<ProxyEntry> getProxies(@NotNull ProxyType type) {
        return this.proxies.get(type);
    }

    public @Nullable Pair<Group, ProxyEntry> getProxy(@NotNull ProxyType type) {
        return new Pair<>(type.getGroup(), (type == ProxyType.NONE) ? NONE : random(this.proxies.get(type)));
    }

    public @Nullable Pair<Group, ProxyEntry> getProxy(@NotNull String query) {

        try {

            String[] split = query.split(":");
            ProxyType proxyType = valueOf(split[0].toUpperCase());
            ProxyCountry proxyCountry = (split.length >= 2) ?
                    ProxyCountry.valueOf(split[1].toUpperCase()) : null;

            if (proxyType == ProxyType.NONE)
                return new Pair<>(proxyType.getGroup(), NONE);

            return new Pair<>(proxyType.getGroup(), random(this.proxies.get(proxyType).stream()
                    .filter((entry) -> entry.country() == proxyCountry || proxyCountry == null)
                    .toList()
            ));

        } catch (Exception exception) {
            return null;
        }

    }

    public int averageLatency(@NotNull ProxyType proxyType) {
        return this.proxies.get(proxyType).stream()
                .mapToInt(ProxyEntry::latency)
                .sum() / ((this.proxies.get(proxyType).size() == 0) ? 1 : this.proxies.get(proxyType).size());
    }

    protected static @NotNull Collection<Proxy> fetchProxies(@NotNull Proxy.Type type, @NotNull String url) {
        return Unirest.get(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .asString().getBody()
                .lines().map((line) -> new Proxy(type, new InetSocketAddress(line.split(":")[0], parseInt(line.split(":")[1]))))
                .toList();
    }

    protected static double round(@NotNull Double value, @NotNull Integer places) {
        return BigDecimal.valueOf(value)
                .setScale(places, HALF_UP)
                .doubleValue();
    }

    protected static @Nullable <T> T random(@NotNull List<T> list) {
        return list.get(current().nextInt(list.size()));
    }

    protected static final ProxyEntry NONE = new ProxyEntry(NO_PROXY, ProxyType.NONE, UNKNOWN, 0);

}
