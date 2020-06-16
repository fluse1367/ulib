package eu.software4you.minecraft.multiversion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

/*
    USound Version: 1

    This software is created and owned by RandomHashTags, and is licensed under the GNU Affero General Public License v3.0 (https://choosealicense.com/licenses/agpl-3.0/)
    You can only find this software at https://gitlab.com/RandomHashTags/usound
    You can find RandomHashTags (me) at
        Discord - RandomHashTags#1948
        Email - imrandomhashtags@gmail.com
        GitHub - https://github.com/RandomHashTags
        GitLab - https://gitlab.com/RandomHashTags
        MCMarket - https://www.mc-market.org/members/20858/
        PayPal - imrandomhashtags@gmail.com
        SpigotMC - https://www.spigotmc.org/members/randomhashtags.76364/
        Twitter - https://twitter.com/irandomhashtags
 */
public enum USound {
    /*
        <sound>(1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.0)
    */
    AMBIENT_CAVE("AMBIENCE_CAVE", "AMBIENT_CAVE"),
    AMBIENT_UNDERWATER_ENTER("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_ENTER"),
    AMBIENT_UNDERWATER_EXIT("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_EXIT"),
    AMBIENT_UNDERWATER_LOOP("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_LOOP"),
    AMBIENT_UNDERWATER_LOOP_ADDITIONS("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_LOOP_ADDITIONS"),
    AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE"),
    AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE"),
    BLOCK_ANVIL_BREAK("ANVIL_BREAK", "BLOCK_ANVIL_BREAK"),
    BLOCK_ANVIL_DESTROY("ANVIL_DESTROY", "BLOCK_ANVIL_DESTROY"),
    BLOCK_ANVIL_FALL("ANVIL_LAND", "BLOCK_ANVIL_FALL"),
    BLOCK_ANVIL_HIT("ANVIL_HIT", "BLOCK_ANVIL_HIT"),
    BLOCK_ANVIL_PLACE("ANVIL_LAND", "BLOCK_ANVIL_PLACE"),
    BLOCK_ANVIL_STEP("ANVIL_LAND", "BLOCK_ANVIL_STEP"),
    BLOCK_ANVIL_USE("ANVIL_USE", "BLOCK_ANVIL_USE"),
    BLOCK_BEACON_ACTIVATE(null, null, null, null, null, "BLOCK_BEACON_ACTIVATE"),
    BLOCK_BEACON_AMBIENT(null, null, null, null, null, "BLOCK_BEACON_AMBIENT"),
    BLOCK_BEACON_DEACTIVATE(null, null, null, null, null, "BLOCK_BEACON_DEACTIVATE"),
    BLOCK_BEACON_POWER_SELECT(null, null, null, null, null, "BLOCK_BEACON_POWER_SELECT"),
    BLOCK_BREWING_STAND_BREW(null, "BLOCK_BREWING_STAND_BREW"),
    BLOCK_BUBBLE_COLUMN_BUBBLE_POP(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_BUBBLE_POP"),
    BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT"),
    BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE"),
    BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT"),
    BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE"),
    BLOCK_CHEST_CLOSE("CHEST_CLOSE", "BLOCK_CHEST_CLOSE"),
    BLOCK_CHEST_LOCKED(null, "BLOCK_CHEST_LOCKED"),
    BLOCK_CHEST_OPEN("CHEST_OPEN", "BLOCK_CHEST_OPEN"),
    BLOCK_CHORUS_FLOWER_DEATH(null, "BLOCK_CHORUS_FLOWER_DEATH"),
    BLOCK_CHORUS_FLOWER_GROW(null, "BLOCK_CHORUS_FLOWER_GROW"),
    BLOCK_COMPARATOR_CLICK(null, "BLOCK_COMPARATOR_CLICK"),
    BLOCK_CONDUIT_ACTIVATE(null, null, null, null, null, "BLOCK_CONDUIT_ACTIVATE"),
    BLOCK_CONDUIT_AMBIENT(null, null, null, null, null, "BLOCK_CONDUIT_AMBIENT"),
    BLOCK_CONDUIT_AMBIENT_SHORT(null, null, null, null, null, "BLOCK_CONDUIT_AMBIENT_SHORT"),
    BLOCK_CONDUIT_ATTACK_TARGET(null, null, null, null, null, "BLOCK_CONDUIT_ATTACK_TARGET"),
    BLOCK_CONDUIT_DEACTIVATE(null, null, null, null, null, "BLOCK_CONDUIT_DEACTIVATE"),
    BLOCK_CORAL_BLOCK_BREAK(null, null, null, null, null, "BLOCK_CORAL_BLOCK_BREAK"),
    BLOCK_CORAL_BLOCK_FALL(null, null, null, null, null, "BLOCK_CORAL_BLOCK_FALL"),
    BLOCK_CORAL_BLOCK_HIT(null, null, null, null, null, "BLOCK_CORAL_BLOCK_HIT"),
    BLOCK_CORAL_BLOCK_PLACE(null, null, null, null, null, "BLOCK_CORAL_BLOCK_PLACE"),
    BLOCK_CORAL_BLOCK_STEP(null, null, null, null, null, "BLOCK_CORAL_BLOCK_PLACE"),
    BLOCK_DISPENSER_DISPENSE(null, "BLOCK_DISPENSER_DISPENSE"),
    BLOCK_DISPENSER_FAIL(null, "BLOCK_DISPENSER_FAIL"),
    BLOCK_DISPENSER_LAUNCH(null, "BLOCK_DISPENSER_LAUNCH"),
    //
    WEATHER_RAIN("AMBIENCE_RAIN", "WEATHER_RAIN"),
    WATHER_RAIN_ABOVE("AMBIENCE_RAIN", "WEATHER_RAIN_ABOVE"),
    ;
    private static final HashMap<String, USound> inUMemory = new HashMap<>();
    private static final HashMap<String, Sound> inMemory = new HashMap<>();
    private final String version = Bukkit.getVersion();
    private String[] names = new String[7];
    private Sound sound;

    USound(String... names) {
        this.names = names;
        this.sound = getSSound();
    }

    public static USound matchUSound(String name) {
        name = name.toUpperCase();
        if (inUMemory.containsKey(name)) return inUMemory.get(name);
        for (USound u : USound.values()) {
            for (String n : u.names) {
                if (n != null && n.equals(name)) {
                    inUMemory.put(name, u);
                    return u;
                }
            }
        }
        return null;
    }

    public static Sound matchSound(String name) {
        name = name.toUpperCase();
        if (inMemory.containsKey(name)) return inMemory.get(name);
        for (USound u : USound.values()) {
            for (String n : u.names) {
                if (n != null && n.equals(name)) {
                    final Sound s = u.getSound();
                    inMemory.put(name, s);
                    return s;
                }
            }
        }
        return null;
    }

    public Sound getSound() {
        return sound;
    }

    public void playSound(Player player, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void playSound(Location l, float volume, float pitch) {
        l.getWorld().playSound(l, sound, volume, pitch);
    }

    // 0 = 1.8.8
    // 1 = 1.9.4
    // 2 = 1.10.2
    // 3 = 1.11.2
    // 4 = 1.12.2
    // 5 = 1.13.2
    // 6 = 1.14.0
    private Sound getSSound() {
        final int ver = version.contains("1.8") ? 0 : version.contains("1.9") ? 1 : version.contains("1.10") ? 2 : version.contains("1.11") ? 3 : version.contains("1.12") ? 4 : version.contains("1.13") ? 5 : version.contains("1.14") ? 6 : names.length - 1;
        int realver = names.length <= ver ? names.length - 1 : ver;
        if (names[realver] == null) {
            boolean did = false;
            for (int i = realver; i >= 0; i--) {
                if (!did && names[i] != null) {
                    realver = i;
                    did = true;
                }
            }
        }
        final String t = names[realver], t2 = names.length > ver ? names[ver] : names[names.length - 1];
        return Sound.valueOf(t != null ? t : t2 != null ? t2 : "AIR");
    }
}
