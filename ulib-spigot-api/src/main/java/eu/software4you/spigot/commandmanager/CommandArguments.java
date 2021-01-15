package eu.software4you.spigot.commandmanager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandArguments {
    private final String[] arguments;
    private final int length;

    private CommandArguments(String[] arguments) {
        this.arguments = arguments;
        this.length = this.arguments.length;
    }

    public static CommandArguments getArguments(String[] arguments, int start) {
        String[] newArray = new String[arguments.length - start];
        if (arguments.length - start >= 0)
            System.arraycopy(arguments, start, newArray, start - start, arguments.length - start);
        return new CommandArguments(newArray);
    }

    public String getString(int index) {
        return this.arguments[index];
    }

    public boolean isEmpty() {
        return this.length < 1;
    }

    public boolean isInteger(int index) {
        try {
            Integer.valueOf(this.arguments[index]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isDouble(int index) {
        try {
            Double.valueOf(this.arguments[index]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isUUID(int index) {
        try {
            getUUID(index);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public boolean isPlayer(int index) {
        return getPlayer(index) != null;
    }

    public boolean isOfflinePlayer(int index) {
        return getOfflinePlayer(index) != null;
    }

    public String[] getArguments() {
        return this.arguments;
    }

    public int getLength() {
        return this.length;
    }

    public int getInteger(int index) {
        return Integer.valueOf(this.arguments[index]);
    }

    public double getDouble(int index) {
        return Double.valueOf(this.arguments[index]);
    }

    public UUID getUUID(int index) {
        return UUID.fromString(this.arguments[index]);
    }

    public Player getPlayer(int index) {
        return isUUID(index) ? Bukkit.getPlayer(getUUID(index)) : Bukkit.getPlayer(this.arguments[index]);
    }

    public OfflinePlayer getOfflinePlayer(int index) {
        return isUUID(index) ? Bukkit.getOfflinePlayer(getUUID(index)) : Bukkit.getOfflinePlayer(this.arguments[index]);
    }
}
