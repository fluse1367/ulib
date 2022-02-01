package eu.software4you.bungeecord.player;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.util.CaseInsensitiveSet;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * A dummy player with permission check functionality intended for use if the actual player is current offline.
 */
public class PermissionsDummyProxiedPlayer extends DummyProxiedPlayer {
    private final ProxyServer bungee;
    private final Collection<String> groups = new CaseInsensitiveSet();
    private final Collection<String> permissions = new CaseInsensitiveSet();

    public PermissionsDummyProxiedPlayer(UUID uniqueId) {
        super(uniqueId);
        bungee = ProxyServer.getInstance();
    }

    @Override
    public Collection<String> getGroups() {
        return Collections.unmodifiableCollection(groups);
    }

    @Override
    public void addGroups(String... groups) {
        for (String group : groups) {
            this.groups.add(group);
            for (String permission : bungee.getConfigurationAdapter().getPermissions(group)) {
                setPermission(permission, true);
            }
        }
    }

    @Override
    public void removeGroups(String... groups) {
        for (String group : groups) {
            this.groups.remove(group);
            for (String permission : bungee.getConfigurationAdapter().getPermissions(group)) {
                setPermission(permission, false);
            }
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return bungee.getPluginManager().callEvent(new PermissionCheckEvent(this, permission, permissions.contains(permission))).hasPermission();
    }

    @Override
    public void setPermission(String permission, boolean value) {
        if (value) {
            permissions.add(permission);
        } else {
            permissions.remove(permission);
        }
    }

    @Override
    public Collection<String> getPermissions() {
        return Collections.unmodifiableCollection(permissions);
    }

}
