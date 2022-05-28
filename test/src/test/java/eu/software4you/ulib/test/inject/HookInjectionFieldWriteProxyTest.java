package eu.software4you.ulib.test.inject;

import eu.software4you.ulib.core.inject.*;
import org.junit.Assert;
import org.junit.Test;

public class HookInjectionFieldWriteProxyTest {

    private String myString = "My Test String";

    @Test
    public void testFieldWriteProxy() throws Exception {
        // test normal
        Assert.assertEquals("My Test String", myString);
        someOrdinaryMethod();
        Assert.assertEquals("My other Test String", myString);
        // set back
        myString = "My Test String";

        // inject at FIELD_WRITE
        var target = "Leu/software4you/ulib/test/inject/HookInjectionFieldWriteProxyTest;myString;Ljava/lang/String;"; // full target signature of `myString`
        var spec = InjectUtil.createHookingSpec(HookPoint.FIELD_WRITE, target);
        new HookInjection(HookInjectionFieldWriteProxyTest.class)
                .<String>addHook("someOrdinaryMethod()V", spec, (params, cb) -> {
                    // lambda will be run at FIELD_WRITE
                    cb.cancel(); // cancel write

                    // write own
                    myString = "Field Write Proxy";
                })
                .inject().rethrow();

        // run again, now expect "Field Write Proxy"
        someOrdinaryMethod();
        Assert.assertEquals("Field Write Proxy", myString);
    }

    public void someOrdinaryMethod() {
        myString = "My other Test String";
    }

}
