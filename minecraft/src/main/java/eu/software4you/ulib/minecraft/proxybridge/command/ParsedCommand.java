package eu.software4you.ulib.minecraft.proxybridge.command;

import org.jetbrains.annotations.NotNull;

public class ParsedCommand {
    private final Command command;
    private final String[] args;

    ParsedCommand(Command command, String[] args) {
        this.command = command;
        this.args = args;
    }

    @NotNull
    public Command getCommand() {
        return command;
    }

    @NotNull
    public String[] getArgs() {
        return args;
    }

    public byte[] execute(@NotNull String origin) {
        return command.execute(getArgs(), origin);
    }
}
