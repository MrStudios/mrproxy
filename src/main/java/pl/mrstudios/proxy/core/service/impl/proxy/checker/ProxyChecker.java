package pl.mrstudios.proxy.core.service.impl.proxy.checker;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import panda.std.Pair;
import pl.mrstudios.proxy.core.service.impl.proxy.checker.api.Response;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyCountry;
import pl.mrstudios.proxy.core.service.impl.proxy.entry.ProxyEntry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.List.copyOf;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.stream.IntStream.range;
import static kong.unirest.Unirest.get;

public class ProxyChecker {

    private final int threads;
    private final int maxLatency;
    private int processedAmount;
    private final int startAmount;

    private final Queue<ProxyEntry> pending;
    private final List<ProxyEntry> working;

    private Consumer<ProxyEntry> consumer;
    private final ScheduledExecutorService executorService;

    public ProxyChecker(
            @NotNull List<ProxyEntry> proxies,
            @NotNull Integer threads,
            @NotNull Integer maxLatency,
            @NotNull Consumer<ProxyEntry> consumer
    ) {
        this(proxies, threads, maxLatency);
        this.consumer = consumer;
    }

    public ProxyChecker(
            @NotNull List<ProxyEntry> proxies,
            @NotNull Integer threads,
            @NotNull Integer maxLatency
    ) {

        this.threads = threads;
        this.maxLatency = maxLatency;
        this.startAmount = proxies.size();
        this.processedAmount = 0;

        this.working = new ArrayList<>();
        this.pending = new LinkedList<>(proxies);
        this.executorService = newScheduledThreadPool(threads);

    }

    public void start() {
        range(0, this.threads)
                .forEach((i) -> this.executorService.submit(() -> {
                    while (!this.pending.isEmpty()) try {

                        AtomicReference<ProxyEntry> proxy = new AtomicReference<>();
                        synchronized (this.pending) {
                            proxy.set(this.pending.remove());
                            this.processedAmount++;
                        }

                        Pair<ProxyEntry, Response> pair = fetch(proxy.get(), this.maxLatency);
                        ofNullable(this.consumer)
                                .ifPresent((consumer) -> consumer.accept(pair.getFirst()));

                        this.working.add(pair.getFirst());

                    } catch (Exception ignored) {}
                }));
    }

    protected static @NotNull Pair<ProxyEntry, Response> fetch(@NotNull ProxyEntry proxy, @NotNull Integer maxLatency) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(format("https://api.proxychecker.co/?ip=%s", IP_ADDRESS)).openConnection(proxy.proxy());

        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(maxLatency);
        connection.setReadTimeout(maxLatency);

        long start = currentTimeMillis();
        connection.connect();
        long latency = currentTimeMillis() - start;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

            Response response = GSON.fromJson(
                    bufferedReader.lines()
                            .collect(Collectors.joining()),
                    Response.class
            );

            return new Pair<>(new ProxyEntry(proxy.proxy(), proxy.type(), ProxyCountry.of(response.country), (int) latency), response);

        }

    }

    public @NotNull List<ProxyEntry> pending() {
        return copyOf(this.pending);
    }

    public @NotNull List<ProxyEntry> working() {
        return this.working;
    }

    public @NotNull ExecutorService executorService() {
        return this.executorService;
    }

    public int avgLatency() {
        return this.working.stream()
                .mapToInt(ProxyEntry::latency)
                .sum() / this.working.size();
    }

    public double progress() {
        return ((double) this.processedAmount / this.startAmount) * 100;
    }

    public boolean isDone() {
        return this.pending.isEmpty();
    }

    protected static final Gson GSON = new Gson();
    protected static final String IP_ADDRESS = get("https://checkip.amazonaws.com/")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            .asString().getBody();

}
