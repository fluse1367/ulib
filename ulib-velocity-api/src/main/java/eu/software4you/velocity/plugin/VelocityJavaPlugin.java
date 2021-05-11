package eu.software4you.velocity.plugin;

import com.google.common.collect.Multimap;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.ulib.minecraft.plugin.Layout;
import eu.software4you.utils.FileUtils;
import eu.software4you.utils.IOUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.audience.Audience;
import org.slf4j.Logger;
import ulib.ported.org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class VelocityJavaPlugin implements VelocityPlugin {

    private final static String layoutBaseName = "layout";
    private final static String layoutFileExtension = "yml";
    private final static String defaultLayoutFileName = String.format("%s.%s", layoutBaseName, layoutFileExtension);
    @Getter
    private final String id;
    @Getter
    private final ProxyServer proxyServer;
    @Getter
    private final Logger logger;
    @Getter
    private final File dataFolder;
    @Getter
    private final File file = FileUtils.getClassFile(getClass());
    private final ConfigurationWrapper configWrapper = new ConfigurationWrapper(null);
    private final Layout<Audience> layout = new VelocityLayout(null);
    private String layoutFileName = defaultLayoutFileName;

    private PluginContainer getPlugin() {
        return proxyServer.getPluginManager().getPlugin(id)
                .orElseThrow(() -> new IllegalStateException("Invalid Plugin ID"));
    }

    @Override
    public String getName() {
        return getPlugin().getDescription().getName().orElse(null);
    }

    @Override
    public void saveDefaultConfig() {
        if (!new File(getDataFolder(), "config.yml").exists())
            saveResource("config.yml", false);
    }

    @Override
    public ConfigurationWrapper getConf() {
        if (configWrapper.section() == null)
            reloadConfig();
        return configWrapper;
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        configWrapper.setSection(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml")));
    }

    @Override
    public void saveDefaultLayout() {
        if (!new File(getDataFolder(), layoutFileName).exists())
            saveResource(layoutFileName, false);
    }

    @Override
    public Layout<Audience> getLayout() {
        if (layout.section() == null)
            reloadLayout();
        return layout;
    }

    @Override
    public void reloadLayout() {
        saveDefaultLayout();
        File layoutFile = new File(getDataFolder(), layoutFileName);
        layout.setSection(YamlConfiguration.loadConfiguration(layoutFile));
    }

    @Override
    public void setLayoutLocale(Locale locale) {
        if (locale == null || locale.getLanguage().isEmpty()) {
            layoutFileName = defaultLayoutFileName;
        } else {
            layoutFileName = String.format("%s.%s.%s", layoutBaseName, locale.getLanguage(), layoutFileExtension);
        }
        reloadLayout();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile().getPath());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                IOUtil.write(in, new FileOutputStream(outFile));
            } else {
                logger.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.error("Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Override
    public ScheduledTask async(Runnable runnable) {
        return proxyServer.getScheduler().buildTask(this, runnable).schedule();
    }

    @Override
    public ScheduledTask async(Runnable runnable, long delay, TimeUnit unit) {
        return proxyServer.getScheduler().buildTask(this, runnable)
                .delay(delay, unit)
                .schedule();
    }

    @Override
    public ScheduledTask async(Runnable runnable, long delay, long period, TimeUnit unit) {
        return proxyServer.getScheduler().buildTask(this, runnable)
                .delay(delay, unit)
                .repeat(period, unit)
                .schedule();
    }

    @SneakyThrows
    @Override
    public void cancelAllTasks() {
        ((Multimap<Object, ScheduledTask>) ReflectUtil.forceCall("com.velocitypowered.proxy.scheduler.VelocityScheduler",
                proxyServer.getScheduler(), "tasksByPlugin")).get(this).forEach(ScheduledTask::cancel);
    }

    @Override
    public void registerEvents(Object listener) {
        proxyServer.getEventManager().register(this, listener);
    }

    @Override
    public void unregisterEvents(Object listener) {
        proxyServer.getEventManager().unregisterListener(this, listener);
    }

    @Override
    public void unregisterAllEvents() {
        proxyServer.getEventManager().unregisterListeners(this);
    }
}
