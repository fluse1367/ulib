package eu.software4you.ulib.minecraft.proxybridge.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class CommandManager {
    private final HashMap<String, Command> commands = new HashMap<>();

    public void registerCommand(@NotNull Command command) {
        if (!commands.containsKey(command.getName()))
            commands.put(command.getName(), command);
    }

    public void unregisterCommand(@NotNull Command command) {
        unregisterCommand(command.getName());
    }

    public void unregisterCommand(@NotNull String name) {
        commands.remove(name);
    }

    @Nullable
    protected Command getCommand(@NotNull String name) {
        return commands.get(name);
    }

    @NotNull
    protected Optional<ParsedCommand> parseCommand(@NotNull String data) {
        String name = data.contains(" ") ? data.substring(0, data.indexOf(" ")) : data;
        if (!commands.containsKey(name))
            return Optional.empty();
        Command command = getCommand(name);
        String[] args = data.contains(" ") ? data.substring(data.indexOf(" ") + 1).split(" ") : new String[0];
        return Optional.of(new ParsedCommand(command, args));
    }
}
