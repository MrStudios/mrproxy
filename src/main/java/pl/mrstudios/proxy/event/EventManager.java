package pl.mrstudios.proxy.event;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.interfaces.Listener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {

    protected final Map<Class<? extends Event>, Map<Method, Listener>> listeners;

    public EventManager() {
        this.listeners = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void register(@NotNull Listener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter((method) -> method.isAnnotationPresent(EventHandler.class))
                .peek((method) -> method.setAccessible(true))
                .forEach(
                        (method) -> this.listeners.computeIfAbsent((Class<? extends Event>) method.getParameterTypes()[0], (key) -> new HashMap<>())
                                .put(method, listener)
                );
    }

    public void call(@NotNull Event event) {
        this.listeners.computeIfAbsent(event.getClass(), (key) -> new HashMap<>())
                .forEach((method, listener) -> {
                    try {
                        method.invoke(listener, event);
                    } catch (Exception exception) {
                        throw new RuntimeException("Unable to invoke " + method.getName() + " method due to exception.", exception);
                    }
                });
    }

}
