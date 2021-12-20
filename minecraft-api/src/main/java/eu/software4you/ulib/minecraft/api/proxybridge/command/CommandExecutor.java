package eu.software4you.ulib.minecraft.api.proxybridge.command;

public interface CommandExecutor {
    byte[] execute(String[] args, String origin);
}
