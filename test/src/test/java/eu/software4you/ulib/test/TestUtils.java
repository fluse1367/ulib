package eu.software4you.ulib.test;

import eu.software4you.ulib.core.function.Task;
import org.junit.Assert;

import java.io.*;

public class TestUtils {

    /**
     * Captures stdout while running a certain task.
     *
     * @param task the task to tun
     * @return the captured data
     */
    public static <X extends Exception> InputStream captureStdOut(Task<X> task) throws X, IOException {

        // capture stdout
        final var stdout = System.out;

        // wrap
        var buf = new PipedInputStream();
        try (var out = new PrintStream(new PipedOutputStream(buf)) {
            @Override
            public void write(int b) {
                super.write(b);
                stdout.write(b);
            }

            @Override
            public synchronized void flush() {
                super.flush();
                stdout.flush();
            }
        }) {
            System.setOut(out);

            // run
            task.execute();
        } finally {
            // revert to original stdout
            System.setOut(stdout);
        }

        return buf;
    }

    /**
     * Runs a task and asserts the output from stdout using {@link Assert#assertArrayEquals(Object[], Object[])}
     *
     * @param task  the task to run
     * @param lines the expected output lines
     */
    public static <X extends Exception> void assertStdOut(Task<X> task, String... lines) throws X, IOException {

        try (var buf = new BufferedReader(new InputStreamReader(captureStdOut(task)))) {
            var readLines = buf.lines().toArray(String[]::new);
            Assert.assertArrayEquals(lines, readLines);
        }

    }

}
