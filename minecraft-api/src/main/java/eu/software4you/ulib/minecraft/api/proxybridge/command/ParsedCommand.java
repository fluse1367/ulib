package eu.software4you.ulib.minecraft.api.proxybridge.command;

public class ParsedCommand {
    private final Command command;
    private final String[] args;

    ParsedCommand(Command command, String[] args) {
        this.command = command;
        this.args = args;
    }

    public Command getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    public byte[] execute(String origin) {
        return command.execute(getArgs(), origin);
    }
}
