package eu.software4you.ulib.spigotbungeecord.bridge.command;

public class Command {
    private final String name;

    private CommandExecutor executor;

    public Command(String name) {
        this.name = name;
    }

    public Command(String name, CommandExecutor executor) {
        this.name = name;
        this.executor = executor;
    }

    public byte[] execute(String[] args) {
        return executor.execute(args);
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public String getName() {
        return name;
    }
}
