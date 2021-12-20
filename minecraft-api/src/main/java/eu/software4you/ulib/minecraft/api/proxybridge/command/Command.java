package eu.software4you.ulib.minecraft.api.proxybridge.command;

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

    public byte[] execute(String[] args, String origin) {
        return executor.execute(args, origin);
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public String getName() {
        return name;
    }
}
