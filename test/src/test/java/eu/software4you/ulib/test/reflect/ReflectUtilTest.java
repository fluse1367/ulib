package eu.software4you.ulib.test.reflect;

import eu.software4you.ulib.core.reflect.ReflectUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReflectUtilTest {

    // test get caller

    @Test
    public void testGetCaller() {
        assertEquals(ReflectUtilTest.class, AnotherTest.test());
        assertEquals(AnotherTest.class, AnotherTest.test2());
    }

    private static final class AnotherTest {
        private static Class<?> test() {
            return ReflectUtil.getCallerClass();
        }

        private static Class<?> test2() {
            return test();
        }
    }
}
