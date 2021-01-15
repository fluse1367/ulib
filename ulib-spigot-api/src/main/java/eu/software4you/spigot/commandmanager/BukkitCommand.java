package eu.software4you.spigot.commandmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BukkitCommand extends Command {
    private CommandExecutor executor;

    protected BukkitCommand(final String name) {
        super(name);
        this.executor = null;
    }

    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (this.executor != null) {
            this.executor.onCommand(sender, this, commandLabel, args);
        }
        return false;
    }

    public void setExecutor(final CommandExecutor exe) {
        this.executor = exe;
    }
}
