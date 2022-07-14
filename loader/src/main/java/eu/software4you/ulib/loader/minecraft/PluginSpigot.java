package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.impl.init.InitAccess;
import eu.software4you.ulib.loader.install.Installer;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.util.Optional;
import java.util.function.Consumer;

public class PluginSpigot extends JavaPlugin {
    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.SPIGOT);
        Installer.installMe();
    }

    private Plugin pluginSubstitute;

    @SneakyThrows
    @Override
    public void onLoad() {
        this.pluginSubstitute = (Plugin) InitAccess.getInstance().construct("spigot", "eu.software4you.ulib.spigot.impl.PluginSubst",
                this, getPluginLoader(), getDescription(), getDataFolder(), getFile());

        // Spigot based servers use a separate class loader for each plugin, so we need to privilege each plugin that requires ulib individually.
        try {
            var spec = InjectUtil.createHookingSpec(HookPoint.METHOD_CALL, "Lorg/bukkit/plugin/java/JavaPlugin;setEnabled(Z)V");
            new HookInjection()
                    // privilege at plugin pre-enable
                    .addHook(JavaPluginLoader.class.getMethod("enablePlugin", Plugin.class), spec, (__, cb) ->
                            filter(cb.proxyInst(), p -> InitAccess.getInstance().privileged(p.getClass().getClassLoader(), true)))

                    // un-privilege at plugin post-disable
                    .addHook(JavaPluginLoader.class.getMethod("disablePlugin", Plugin.class), spec, (__, cb) ->
                            filter(cb.proxyInst(), p -> InitAccess.getInstance().privileged(p.getClass().getClassLoader(), false)))

                    .inject()
                    .rethrowRE();
        } catch (NoSuchMethodException e) {
            throw new InternalError(e);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void filter(Optional<Object> pluginOpt, Consumer<Plugin> consumer) {
        if (!(pluginOpt.orElse(null) instanceof Plugin plugin))
            return;

        var desc = plugin.getDescription();
        if (!desc.getDepend().contains(getName()) && !desc.getSoftDepend().contains(getName()))
            return;

        consumer.accept(plugin);
    }

    @Override
    public void onEnable() {
        pluginSubstitute.onEnable();

        Optional.ofNullable(getCommand("ulib"))
                .orElseThrow()
                .setExecutor((sender, command, label, args) -> {
                    sender.sendMessage("%suLib version %s".formatted(ChatColor.GREEN, getDescription().getVersion()));
                    return true;
                });
    }

    @Override
    public void onDisable() {
        pluginSubstitute.onDisable();
    }
}
