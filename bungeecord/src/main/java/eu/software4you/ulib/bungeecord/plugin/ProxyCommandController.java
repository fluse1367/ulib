package eu.software4you.ulib.bungeecord.plugin;

import net.md_5.bungee.api.plugin.Command;

/**
 * Controller for command management.
 */
public interface ProxyCommandController {
    /**
     * Registers a command.
     *
     * @param command the command to register
     */
    void registerCommand(Command command);

    /**
     * Removes a command's registration.
     *
     * @param command the command whose registration to remove
     */
    void unregisterCommand(Command command);

    /**
     * Attempts to remove all registrations from every registered command.
     */
    void unregisterAllCommands();
}
