package eu.software4you.ulib.spigot.api;

import org.bukkit.ChatColor;

public class McStringUtils {

    public static String colorText(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
