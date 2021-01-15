package eu.software4you.spigot;

import eu.software4you.spigot.plugin.ExtendedPlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class ScoreboardManager {

    private static ExtendedPlugin plugin;

    public static void setPlugin(ExtendedPlugin plugin) {
        if(ScoreboardManager.plugin != null)
            throw new IllegalStateException(String.format("Scoreboard manager already initialized by %s", plugin.getDescription().getName()));
        ScoreboardManager.plugin = plugin;
    }

    private static HashMap<String, ScoreboardManager> instances = new HashMap<>();
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
        objective = scoreboard.registerNewObjective(p.getName(), "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(McStringUtils.colorText(title));

        for (String entry : sidebarEntries.keySet()) {
            objective.getScore(McStringUtils.colorText(entry)).setScore(sidebarEntries.get(entry));
        }

        p.setScoreboard(scoreboard);
    }

    public void updateSidebarEntry(Player p, String id, String score, String prefix, String suffix) {
        Scoreboard scoreboard = getScoreboard(p);
        Team team = scoreboard.getTeam(id);
        if (team == null)
            team = scoreboard.registerNewTeam(id);
        if (!team.hasEntry(McStringUtils.colorText(score)))
            team.addEntry(McStringUtils.colorText(score));
        team.setPrefix(McStringUtils.colorText(prefix));
        team.setSuffix(McStringUtils.colorText(suffix));
    }
}
