package eu.software4you.ulib.minecraft.proxybridge.command;

import org.jetbrains.annotations.NotNull;

public class Command {
    private final String name;

    private CommandExecutor executor;

    public Command(@NotNull String name) {
        this.name = name;
    }

    public Command(@NotNull String name, @NotNull CommandExecutor executor) {
        this.name = name;
        this.executor = executor;
    }

    public byte[] execute(@NotNull String[] args, @NotNull String origin) {
        return executor.execute(args, origin);
    }

    public void setExecutor(@NotNull CommandExecutor executor) {
        this.executor = executor;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
