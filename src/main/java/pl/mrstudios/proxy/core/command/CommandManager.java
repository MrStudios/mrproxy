package pl.mrstudios.proxy.core.command;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class CommandManager {

    private Map<String, Command> commands;

    public CommandManager() {
        this.commands = new ConcurrentHashMap<>();
    }

    public void register(@NotNull String name, @NotNull Command command) {
        this.commands.put(name, command);
    }

    public void unregister(@NotNull String name) {
        this.commands.remove(name);
    }

    public @NotNull Map<String, Command> getCommands() {
        return this.commands;
    }

}
