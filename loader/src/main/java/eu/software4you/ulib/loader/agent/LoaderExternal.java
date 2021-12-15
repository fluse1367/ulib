package eu.software4you.ulib.loader.agent;

import com.sun.tools.attach.VirtualMachine;
import lombok.SneakyThrows;

/*
This class is designed to be started in a dedicated java process.
It loads the AgentMain and attaches it to the original java instance.
 */
public class LoaderExternal {
    @SneakyThrows
    public static void main(String[] args) {
        String pid = args[0];
        String agentPath = args[1];

        // attach this extra process to original jvm
        VirtualMachine vm = VirtualMachine.attach(pid);

        // load agent!
        vm.loadAgent(agentPath);
    }
}
