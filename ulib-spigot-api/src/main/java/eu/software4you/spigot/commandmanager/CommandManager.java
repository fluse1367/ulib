package eu.software4you.spigot.commandmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class CommandManager implements CommandExecutor {
    private HashMap<BaseCommand, MethodContainer> cmds;
    private CommandMap cmap;
    private JavaPlugin plugin;

    public CommandManager(JavaPlugin plugin) {
        this.cmds = new HashMap<>();
        this.plugin = plugin;
        CommandMap map;
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            map = (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception ex) {
            map = null;
            ex.printStackTrace();
        }
        this.cmap = map;
    }

    private void registerCommand(String name, String[] aliases) {
        if (this.cmap.getCommand(name) != null) {
            return;
        }
        BukkitCommand cmd = new BukkitCommand(name);
        if (aliases.length > 0)
            cmd.setAliases(Arrays.asList(aliases));
        this.cmap.register(this.plugin.getName().toLowerCase(), cmd);
        cmd.setExecutor(this);
    }

    private BaseCommand getCommand(Command c, CommandArguments args, BaseCommand.Sender sender) {
        BaseCommand ret = null;
        for (BaseCommand bc : this.cmds.keySet()) {
            if (bc.sender() != sender) {
                continue;
            }
            if (!bc.command().equalsIgnoreCase(c.getName())) {
                continue;
            }
            if (args.isEmpty() && bc.subCommand().trim().isEmpty()) {
                ret = bc;
            } else {
                if (args.isEmpty() || !bc.subCommand().equalsIgnoreCase(args.getString(0))) {
                    continue;
                }
                ret = bc;
            }
        }
        return ret;
    }

    private Object getCommandObject(Command c, BaseCommand.Sender sender, CommandArguments args) throws Exception {
        Method me =
                getMethod0(c, sender, args);
        return me.getDeclaringClass().newInstance();
    }

    private Method getMethod0(Command c, BaseCommand.Sender sender, CommandArguments args) {
        BaseCommand bcmd = this.getCommand(c, args, sender);
        if (bcmd == null) {
            for (BaseCommand bc : this.cmds.keySet()) {
                if (bc.sender() != sender) {
                    continue;
                }
                if (bc.command().equalsIgnoreCase(c.getName()) && bc.subCommand().trim().isEmpty()) {
                    bcmd = bc;
                    break;
                }
            }
        }
        MethodContainer container = this.cmds.get(bcmd);
        return container.getMethod(sender);
    }

    public void registerClass(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(CommandHandler.class)) {
            this.plugin.getLogger().severe("Class is no CommandHandler");
            return;
        }
        HashMap<BaseCommand, HashMap<BaseCommand.Sender, Method>> list = new HashMap<BaseCommand, HashMap<BaseCommand.Sender, Method>>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(BaseCommand.class)) {
                BaseCommand bc = m.getAnnotation(BaseCommand.class);
                this.registerCommand(bc.command(), bc.commandAliases());
                if (!list.containsKey(bc)) {
                    list.put(bc, new HashMap<BaseCommand.Sender, Method>());
                }
                HashMap<BaseCommand.Sender, Method> map = list.get(bc);
                map.put(bc.sender(), m);
                list.remove(bc);
                list.put(bc, map);
            }
        }
        for (BaseCommand command : list.keySet()) {
            HashMap<BaseCommand.Sender, Method> map2 = list.get(command);
            if (this.cmds.containsKey(command)) {
                MethodContainer container = this.cmds.get(command);
                for (BaseCommand.Sender s : container.getMethodMap().keySet()) {
                    Method i = container.getMethod(s);
                    map2.put(s, i);
                }
                this.cmds.remove(command);
            }
            this.cmds.put(command, new MethodContainer(map2));
        }
    }

    private Method getMethod(Command c, BaseCommand.Sender sender, CommandArguments args) {
        try {
            return getMethod0(c, sender, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void executeCommand(Command c, CommandSender s, String[] args) {
        CommandArguments arguments = CommandArguments.getArguments(args, 0);
        BaseCommand.Sender sender;
        if (s instanceof Player) {
            sender = BaseCommand.Sender.PLAYER;
        } else {
            sender = BaseCommand.Sender.CONSOLE;
        }
        Method m = this.getMethod(c, sender, arguments);
        if (m != null) {
            m.setAccessible(true);
            BaseCommand bc = m.getAnnotation(BaseCommand.class);
            if (!bc.subCommand().trim().isEmpty() && bc.subCommand().equalsIgnoreCase(arguments.getString(0))) {
                arguments = CommandArguments.getArguments(args, 1);
            }
            CommandArguments a = arguments;
            Runnable r = () -> {
                Object result;
                try {
                    if (sender == BaseCommand.Sender.PLAYER) {
                        Player p = (Player) s;
                        bc.permission();
                        if (!bc.permission().trim().isEmpty()) {
                            if (!p.hasPermission(bc.permission())) {
                                result = CommandResult.NO_PERMISSION;
                            } else {
                                result = m.invoke(this.getCommandObject(c, sender, a), p, a);
                            }
                        } else {
                            result = m.invoke(this.getCommandObject(c, sender, a), p, a);
                        }
                    } else {
                        result = m.invoke(this.getCommandObject(c, sender, a), s, a);
                    }
                } catch (Exception e) {
                    if (e instanceof InvocationTargetException) {
                        e.getCause().printStackTrace();
                    } else {
                        e.printStackTrace();
                    }
                    result = CommandResult.NONE;
                    s.sendMessage("§cAn unknown error occurred while performing this command! See console for details.");
                }
                CommandResult cr;
                if (result instanceof CommandResult && (cr = ((CommandResult) result)).getMessage() != null && !cr.getMessage().isEmpty()) {
                    s.sendMessage(cr.getMessage().replace("%command%", bc.command()).replace("%permission%", bc.permission()));
                }
            };
            if (bc.aSync()) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, r);
            } else {
                r.run();
            }
        } else {
            s.sendMessage("§4The command was not made for your sender type!");
        }
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        this.executeCommand(cmnd, cs, strings);
        return true;
    }
}
