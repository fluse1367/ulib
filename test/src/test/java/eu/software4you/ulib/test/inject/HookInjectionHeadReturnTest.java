package eu.software4you.ulib.test.inject;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.test.TestUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;

@FixMethodOrder
public class HookInjectionHeadReturnTest {

    private static final String
            TEST_STR = "This is a ordinary method",
            HEAD_TEST_STR = "This is the head of a ordinary method",
            RETURN_TEST_STR = "This is the return of a ordinary method";

    @Test
    public void testHeadInjection() throws Exception {
        // test normal
        TestUtils.assertStdOut(this::someOrdinaryMethod, TEST_STR);

        // inject at HEAD
        new HookInjection(HookInjectionHeadReturnTest.class)
                .addHook("someOrdinaryMethod()V", InjectUtil.createHookingSpec(HookPoint.HEAD), (params, cb) -> {
                    // lambda will be run at HEAD
                    System.out.println(HEAD_TEST_STR);
                })
                .inject().rethrow();

        // run again, now expect HEAD_TEST_STR to be printed first
        TestUtils.assertStdOut(this::someOrdinaryMethod, HEAD_TEST_STR, TEST_STR);
    }

    public void someOrdinaryMethod() {
        System.out.println(TEST_STR);
    }

    @Test
    public void testReturnInjection() throws Exception {
        // test normal
        TestUtils.assertStdOut(this::someOtherOrdinaryMethod, TEST_STR);

        // inject at HEAD
        new HookInjection(HookInjectionHeadReturnTest.class)
                .addHook("someOtherOrdinaryMethod()V", InjectUtil.createHookingSpec(HookPoint.RETURN), (params, cb) -> {
                    // lambda will be run at RETURN
                    System.out.println(RETURN_TEST_STR);
                })
                .inject().rethrow();

        // run again, now expect RETURN_TEST_STR to be printed last
        TestUtils.assertStdOut(this::someOtherOrdinaryMethod, TEST_STR, RETURN_TEST_STR);
    }

    public void someOtherOrdinaryMethod() {
        System.out.println(TEST_STR);
    }

}
