package eu.software4you.ulib.test.inject;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.test.TestUtils;
import org.junit.Test;

public class HookInjectionMethodProxyTest {

    @Test
    public void testMethodProxyInjection() throws Exception {
        // test normal
        TestUtils.assertStdOut(this::someOrdinaryMethod, "Hello World!");

        // inject at METHOD_CALL
        var target = "Leu/software4you/ulib/test/inject/HookInjectionMethodProxyTest;getWorld()Ljava/lang/String;"; // full target signature of #getWorld()
        var spec = InjectUtil.createHookingSpec(HookPoint.METHOD_CALL, target);
        new HookInjection(HookInjectionMethodProxyTest.class)
                .<String>addHook("someOrdinaryMethod()V", spec, (params, cb) -> {
                    // lambda will be run at METHOD_CALL
                    cb.setReturnValue("Method Proxy"); // return "Method Proxy", will also cancel #getWorld()
                })
                .inject().rethrow();

        // run again, now expect "Hello Method Proxy!"
        TestUtils.assertStdOut(this::someOrdinaryMethod, "Hello Method Proxy!");
    }

    public void someOrdinaryMethod() {
        System.out.printf("Hello %s!%n", getWorld());
    }

    public String getWorld() {
        return "World";
    }
}
