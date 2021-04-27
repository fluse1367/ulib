package eu.software4you.ulib;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;

class LoggingFactory {
    private final Properties properties;
    private final Logger logger;
    private final Lib instance;

    LoggingFactory(Properties properties, Logger logger, Lib instance) {
        this.properties = properties;
        this.logger = logger;
        this.instance = instance;
    }

    void prepare() {
        // init logger
        PrintStream err = System.err;
        // make ConsoleHandler use the actual stdout
        System.setErr(System.out);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        // reset System.out to previous one
        System.setErr(err);

        consoleHandler.setFormatter(ansiFormatter());
        consoleHandler.setLevel(properties.LOG_LEVEL);
        logger.addHandler(consoleHandler);

        try {
            if (!properties.DATA_DIR.exists()) {
                if (!properties.DATA_DIR.mkdirs()) {
                    throw new IOException(); // trigger error message
                }
            }

            FileHandler fileHandler = new FileHandler(properties.DATA_DIR.getPath() + "/ulib.%g.log",
                    67108864 /*64 MiB*/, 16);
            fileHandler.setFormatter(normalFormatter());
            fileHandler.setLevel(properties.LOG_LEVEL);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Could not append file handler to logger. All uLib logged records will not be saved to disk.");
            e.printStackTrace();
        }
        logger.setLevel(properties.LOG_LEVEL);
    }

    void systemInstall() {
        org.fusesource.jansi.AnsiConsole.systemInstall();
    }

    private Formatter normalFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder b = new StringBuilder(simpleFormat(record));
                if (record.getThrown() != null)
                    b.append(getStackTrace(record.getThrown())).append("\n");
                return b.toString();
            }
        };
    }

    private Formatter ansiFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder b = new StringBuilder();

                Level level = record.getLevel();

                if (level == Level.SEVERE) {
                    b.append(Escapes.RED);
                } else if (level == Level.WARNING) {
                    b.append(Escapes.YELLOW);
                }

                b.append(simpleFormat(record));

                if (record.getThrown() != null)
                    b.append(Escapes.RED)
                            .append(getStackTrace(record.getThrown())).append("\n");
                b.append(Escapes.RESET);
                return b.toString();
            }
        };
    }

    private String simpleFormat(LogRecord record) {
        String LOG_FORMAT = "[%s] %tT %s: %s\n";
        return String.format(LOG_FORMAT, instance.getNameOnly(),
                new Date(record.getMillis()), record.getLevel().getName(), record.getMessage());
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter wr = new StringWriter();
        throwable.printStackTrace(new PrintWriter(wr));
        return wr.toString();
    }

    private static class Escapes {
        private static final String ESC = "\033";

        // Reset all escapes
        private static final String RESET = ESC + "[0m";

        // Regular Colors
        private static final String BLACK = ESC + "[0;30m";
        private static final String RED = ESC + "[0;31m";
        private static final String GREEN = ESC + "[0;32m";
        private static final String YELLOW = ESC + "[0;33m";
        private static final String BLUE = ESC + "[0;34m";
        private static final String PURPLE = ESC + "[0;35m";
        private static final String CYAN = ESC + "[0;36m";
        private static final String WHITE = ESC + "[0;37m";
    }
}
