package pl.mrstudios.proxy.core.service;

import pl.mrstudios.proxy.event.interfaces.Listener;

import java.time.Duration;

import static java.time.Duration.of;
import static java.time.temporal.ChronoUnit.SECONDS;

public interface Service extends Runnable, Listener {
    default Duration repeatDelay() {
        return of(1, SECONDS);
    }
}
