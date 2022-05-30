package eu.software4you.ulib.minecraft.proxybridge.command;

@FunctionalInterface
public interface CommandExecutor {
    byte[] execute(String[] args, String origin);
}
