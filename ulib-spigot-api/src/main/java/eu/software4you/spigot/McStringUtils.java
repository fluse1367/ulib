package eu.software4you.spigot;

import eu.software4you.ulib.core.api.utils.StringUtils;
import org.bukkit.ChatColor;

public class McStringUtils {

    public static String colorText(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String randomMcColorString(int length) {
        StringBuilder b = new StringBuilder();
        for (String l : StringUtils.randomString(length, "abcdef0123456lnmor").split("|"))
            b.append("§" + l);
        return b.toString();
    }
}
