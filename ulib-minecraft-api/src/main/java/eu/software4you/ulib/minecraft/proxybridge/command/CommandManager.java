package eu.software4you.ulib.minecraft.proxybridge.command;

import java.util.HashMap;

public class CommandManager {
    private final HashMap<String, Command> commands = new HashMap<>();

    public void registerCommand(Command command) {
        if (!commands.containsKey(command.getName()))
            commands.put(command.getName(), command);
    }

    public void unregisterCommand(Command command) {
        unregisterCommand(command.getName());
    }

    public void unregisterCommand(String name) {
        commands.remove(name);
    }

    protected Command getCommand(String name) {
        return commands.get(name);
    }

    protected ParsedCommand parseCommand(String data) {
        String name = data.contains(" ") ? data.substring(0, data.indexOf(" ")) : data;
        if (!commands.containsKey(name))
            throw new IllegalArgumentException(String.format("Command %s was not found", name));
        Command command = getCommand(name);
        String[] args = data.contains(" ") ? data.substring(data.indexOf(" ") + 1).split(" ") : new String[0];
        return new ParsedCommand(command, args);
    }
}
