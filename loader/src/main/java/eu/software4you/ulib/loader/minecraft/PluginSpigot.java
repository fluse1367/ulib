package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Unsafe;
import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.impl.init.InitAccess;
import eu.software4you.ulib.loader.install.Installer;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.util.Optional;

public class PluginSpigot extends JavaPlugin {
    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.SPIGOT);
        Installer.installMe();
    }

    public PluginSpigot() {
        // Spigot based servers use a separate class loader for each plugin, so we need to privilege each plugin that requires ulib individually.
        try {
            new HookInjection()
                    // privilege at plugin pre-construct
                    .addHook(ReflectUtil.forName("org.bukkit.plugin.java.PluginClassLoader", true, Bukkit.class.getClassLoader()).orElseRethrowRE(),
                            "<init>",
                            InjectUtil.createHookingSpec(HookPoint.METHOD_CALL, "Ljava/lang/Class;forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;"),
                            (__, cb) -> {

                                ClassLoader self = (ClassLoader) cb.self().orElseThrow();
                                var desc = ReflectUtil.icall(PluginDescriptionFile.class,
                                        self, "description").orElseRethrow();

                                filter(desc, () -> InitAccess.getInstance().privileged(self, true));
                            })

                    // un-privilege at plugin post-disable
                    .addHook(JavaPluginLoader.class.getMethod("disablePlugin", Plugin.class),
                            InjectUtil.createHookingSpec(HookPoint.METHOD_CALL, "Lorg/bukkit/plugin/java/JavaPlugin;setEnabled(Z)V"),
                            (__, cb) -> {
                                JavaPlugin pl = (JavaPlugin) cb.proxyInst().orElseThrow();

                                // cancel actual method call and artificially call it *before* processing the un-privilegement
                                // -> simulates a post method call hook point
                                cb.cancel();
                                Unsafe.doPrivileged(() -> ReflectUtil.icall(pl, "setEnabled()",
                                        Param.listOf(boolean.class, false)).rethrow());

                                // now process it
                                filter(pl.getDescription(), () -> InitAccess.getInstance().privileged(pl.getClass().getClassLoader(), false));
                            })

                    .inject()
                    .rethrowRE();
        } catch (NoSuchMethodException e) {
            throw new InternalError(e);
        }
    }

    private void filter(PluginDescriptionFile desc, Runnable onSuccess) {
        if (!desc.getDepend().contains(getName()) && !desc.getSoftDepend().contains(getName()))
            return;

        onSuccess.run();
    }

    private Plugin pluginSubstitute;

    @SneakyThrows
    @Override
    public void onLoad() {
        this.pluginSubstitute = (Plugin) InitAccess.getInstance().construct("spigot", "impl.PluginSubst",
                this, getPluginLoader(), getDescription(), getDataFolder(), getFile());
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
