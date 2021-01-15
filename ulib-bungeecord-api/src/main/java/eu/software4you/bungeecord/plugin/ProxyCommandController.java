package eu.software4you.bungeecord.plugin;

import net.md_5.bungee.api.plugin.Command;

public interface ProxyCommandController {
    void registerCommand(Command command);

    void unregisterCommand(Command command);

    void unregisterAllCommands();
}
