package eu.software4you.ulib.bungeecord.plugin;

import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;

/**
 * Controller for command management.
 */
public interface ProxyCommandController {
    /**
     * Registers a command.
     *
     * @param command the command to register
     */
    void registerCommand(@NotNull Command command);

    /**
     * Removes a command's registration.
     *
     * @param command the command whose registration to remove
     */
    void unregisterCommand(@NotNull Command command);

    /**
     * Attempts to remove all registrations from every registered command.
     */
    void unregisterAllCommands();
}
