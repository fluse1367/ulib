package eu.software4you.ulib.loader.agent;

import eu.software4you.ulib.loader.install.Installer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.instrument.Instrumentation;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {
        agentmain(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.getProperties().put("ulib.javaagent", inst);

        switch (System.getProperty("ulib.install.agent_trigger", "")) {
            case "init": {
                Installer.ensureInitialization();
            }
            case "selfinstall": {
                Installer.ensureInitialization();
                Installer.installTo(ClassLoader.getSystemClassLoader());
            }
        }

    }
}
