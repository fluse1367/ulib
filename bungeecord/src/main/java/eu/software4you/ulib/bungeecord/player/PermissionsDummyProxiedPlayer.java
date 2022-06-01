package eu.software4you.ulib.bungeecord.player;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.util.CaseInsensitiveSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A dummy player with permission check functionality intended for use if the actual player is current offline.
 */
public class PermissionsDummyProxiedPlayer extends DummyProxiedPlayer {
    private final ProxyServer bungee;
    private final Collection<String> groups = new CaseInsensitiveSet();
    private final Collection<String> permissions = new CaseInsensitiveSet();

    public PermissionsDummyProxiedPlayer(@NotNull UUID uniqueId) {
        super(uniqueId);
        bungee = ProxyServer.getInstance();
    }

    @Override
    @NotNull
    public Collection<String> getGroups() {
        return Collections.unmodifiableCollection(groups);
    }

    @Override
    public void addGroups(@NotNull String... groups) {
        for (String group : groups) {
            this.groups.add(group);
            for (String permission : bungee.getConfigurationAdapter().getPermissions(group)) {
                setPermission(permission, true);
            }
        }
    }

    @Override
    public void removeGroups(@NotNull String... groups) {
        for (String group : groups) {
            this.groups.remove(group);
            for (String permission : bungee.getConfigurationAdapter().getPermissions(group)) {
                setPermission(permission, false);
            }
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return bungee.getPluginManager().callEvent(new PermissionCheckEvent(this, permission, permissions.contains(permission))).hasPermission();
    }

    @Override
    public void setPermission(@NotNull String permission, boolean value) {
        if (value) {
            permissions.add(permission);
        } else {
            permissions.remove(permission);
        }
    }

    @Override
    @NotNull
    public Collection<String> getPermissions() {
        return Collections.unmodifiableCollection(permissions);
    }

}
