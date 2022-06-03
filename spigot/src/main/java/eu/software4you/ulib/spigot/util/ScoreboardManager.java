package eu.software4you.ulib.spigot.util;

import eu.software4you.ulib.core.impl.BypassAnnotationEnforcement;
import eu.software4you.ulib.spigot.plugin.ExtendedPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.*;

import java.util.HashMap;

/**
 * @deprecated this class is poorly designed
 */
// TODO: re-do this class
@Deprecated(since = "3.0")
@BypassAnnotationEnforcement
public class ScoreboardManager {

    private static ExtendedPlugin plugin;

    public static void setPlugin(ExtendedPlugin plugin) {
        if (ScoreboardManager.plugin != null)
            throw new IllegalStateException(String.format("Scoreboard manager already initialized by %s", plugin.getDescription().getName()));
        ScoreboardManager.plugin = plugin;
    }

    private static final HashMap<String, ScoreboardManager> instances = new HashMap<>();
    private final String name;

    public ScoreboardManager(String name) {
        this.name = name;
    }

    public static ScoreboardManager getInstance(String name) {
        if (!instances.containsKey(name))
            instances.put(name, new ScoreboardManager(name));
        return instances.get(name);
    }

    public Scoreboard getScoreboard(Player p) {
        if (!p.hasMetadata(this.name)) {
            Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
            p.setMetadata(this.name, new FixedMetadataValue(plugin, scoreboard));
        }
        return (Scoreboard) p.getMetadata(this.name).get(0).value();
    }

    public void setSidebar(Player p, String title, HashMap<String, Integer> sidebarEntries) {

        Scoreboard scoreboard = getScoreboard(p);
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective != null)
            objective.unregister();
        objective = scoreboard.registerNewObjective(p.getName(), "dummy", colorText(title));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (String entry : sidebarEntries.keySet()) {
            objective.getScore(colorText(entry)).setScore(sidebarEntries.get(entry));
        }

        p.setScoreboard(scoreboard);
    }

    public void updateSidebarEntry(Player p, String id, String score, String prefix, String suffix) {
        Scoreboard scoreboard = getScoreboard(p);
        Team team = scoreboard.getTeam(id);
        if (team == null)
            team = scoreboard.registerNewTeam(id);
        if (!team.hasEntry(colorText(score)))
            team.addEntry(colorText(score));
        team.setPrefix(colorText(prefix));
        team.setSuffix(colorText(suffix));
    }

    private String colorText(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
