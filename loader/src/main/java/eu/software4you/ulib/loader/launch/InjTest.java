package eu.software4you.ulib.loader.launch;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.loader.install.Installer;
import lombok.SneakyThrows;

public class InjTest {

    @SneakyThrows
    public static void test() {
        Installer.installMe();

        {
            if (true) /* just a switch */ {
                // automatic injection does not work
                new HookInjection()
                        .addHook(InjTest.class, new InjTest())
                        .inject().rethrow();

            } else {
                // manual injection works
                var in = new InjTest();

                var target = InjTest.class.getMethod("proxy_calc", int.class, int.class, Callback.class)
                        .getAnnotation(Hook.class).spec();

                new HookInjection(InjTest.class)
                        .<Integer>addHook("testCalc()V", target, (params, cb) -> {
                            in.proxy_calc((int) params[0], (int) params[1], cb);
                        })
                        .inject().rethrow();
            }

            testCalc();
        }

        if (true) return;

        // test method proxy
        {
            System.out.println("Test method proxy");
            InjTest.test1(); // should print out "Hello World!"

            var target = InjectUtil.createHookingSpec(HookPoint.METHOD_CALL, "Leu/software4you/ulib/loader/launch/InjTest;getWorld()Ljava/lang/String;");
            new HookInjection(InjTest.class)
                    .<String>addHook(InjTest.class.getDeclaredMethod("test1"), target, (params, cb) -> {
                        cb.setReturnValue("Method Proxy");
                    })
                    .inject().rethrow();

            InjTest.test1(); // should print out "Hello Method Proxy!"
        }

        // test field read proxy
        {
            System.out.println("Test field read proxy");
            InjTest.test2(); // should print out "Hello World!"

            var target = InjectUtil.createHookingSpec(HookPoint.FIELD_READ, "Leu/software4you/ulib/loader/launch/InjTest;world;Ljava/lang/String;");
            new HookInjection(InjTest.class)
                    .<String>addHook(InjTest.class.getDeclaredMethod("test2"), target, (params, cb) -> {
                        cb.setReturnValue("Field Read Proxy");
                    })
                    .inject().rethrow();

            InjTest.test2(); // should print out "Hello Field Read Proxy!"
        }

        // test field write proxy
        {
            System.out.println("Test field write proxy");
            InjTest.test3(); // should print out "Hello World!"
            InjTest.clearTest3();

            var target = InjectUtil.createHookingSpec(HookPoint.FIELD_WRITE, "Leu/software4you/ulib/loader/launch/InjTest;world;Ljava/lang/String;");
            new HookInjection(InjTest.class)
                    .<String>addHook(InjTest.class.getDeclaredMethod("test3"), target, (params, cb) -> {
                        cb.cancel(); // cancel original write

                        var r = "Field Write Proxy";
                        System.out.printf("Attempting to write '%s' into `world`, replacing with `%s`%n", cb.getReturnValue(), r);

                        // write own value
                        InjTest.world = r;
                    })
                    .inject().rethrow();

            InjTest.test3(); // should print out "Hello Field Write Proxy!"
        }

    }

    @Hook(value = "testCalc()V", clazz = "eu.software4you.ulib.loader.launch.InjTest",
            spec = @Spec(point = HookPoint.METHOD_CALL, target = "Leu/software4you/ulib/loader/launch/InjTest;calc(II)I"))
    public void proxy_calc(int a, int b, Callback<Integer> callback) {
        var r = calc(2 * a, 2 * b);
        System.out.println("Returning " + r);
        callback.setReturnValue(r);
    }

    public static void testCalc() {
        int i = calc(1, 2);
        System.out.println(i);
    }

    public static int calc(int a, int b) {
        return a + b;
    }

    private static void test1() {
        System.out.printf("Hello %s!%n", getWorld());
    }

    private static String getWorld() {
        return "World";
    }

    private static String world = "World";

    private static void test2() {
        System.out.printf("Hello %s!%n", world);
    }

    private static void test3() {
        world = "Other World";

        System.out.printf("Hello %s!%n", world);
    }

    private static void clearTest3() {
        world = "World";
    }
}
