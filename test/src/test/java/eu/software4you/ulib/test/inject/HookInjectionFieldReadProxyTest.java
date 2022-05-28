package eu.software4you.ulib.test.inject;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.test.TestUtils;
import org.junit.Test;

public class HookInjectionFieldReadProxyTest {

    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private static String world = "World";

    @Test
    public void testFieldReadProxy() throws Exception {
        // test normal
        TestUtils.assertStdOut(this::someOrdinaryMethod, "Hello World!");

        // inject at FIELD_READ
        var target = "Leu/software4you/ulib/test/inject/HookInjectionFieldReadProxyTest;world;Ljava/lang/String;"; // full target signature of `world`
        var spec = InjectUtil.createHookingSpec(HookPoint.FIELD_READ, target);
        new HookInjection(HookInjectionFieldReadProxyTest.class)
                .<String>addHook("someOrdinaryMethod()V", spec, (params, cb) -> {
                    // lambda will be run at FIELD_READ
                    cb.setReturnValue("Field Read Proxy"); // return "Field Read Proxy"
                })
                .inject().rethrow();

        // run again, now expect "Hello Field Read Proxy!"
        TestUtils.assertStdOut(this::someOrdinaryMethod, "Hello Field Read Proxy!");
    }


    public void someOrdinaryMethod() {
        System.out.printf("Hello %s!%n", world);
    }

}
