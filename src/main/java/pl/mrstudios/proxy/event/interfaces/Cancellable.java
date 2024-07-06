package pl.mrstudios.proxy.event.interfaces;

public interface Cancellable {
    boolean isCancelled();
    void setCancelled(boolean value);
}
