package eu.software4you.ulib;

import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

class LoggingFactory {
    private final Properties properties;
    private final Lib instance;
    private final Logger logger;

    private LoggingFactory(Properties properties, Lib instance) {
        this.properties = properties;
        this.instance = instance;

        this.logger = Logger.getLogger(instance.getClass().getName());
        this.logger.setUseParentHandlers(false);
    }

    static Logger fabricate(Properties properties, LibImpl instance) {
        LoggingFactory factory = new LoggingFactory(properties, instance);
        factory.terminalInit();
        factory.fileInit();
        factory.logger.setLevel(properties.LOG_LEVEL);
        AnsiConsole.systemInstall();

        return factory.logger;
    }

    private void terminalInit() {
        PrintStream err = System.err;
        // make ConsoleHandler use the actual stdout
        System.setErr(System.out);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        // reset System.err to previous one
        System.setErr(err);

        consoleHandler.setFormatter(ansiFormatter());
        consoleHandler.setLevel(properties.LOG_LEVEL);
        logger.addHandler(consoleHandler);
    }

    private void fileInit() {
        try {
            FileHandler fileHandler = new FileHandler(properties.LOGS_DIR.getPath() + "/ulib.%g.log",
                    67108864 /*64 MiB*/, 16);
            fileHandler.setFormatter(normalFormatter());
            fileHandler.setLevel(properties.LOG_LEVEL);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Could not append file handler to logger. All uLib logged records will not be saved to disk.");
            e.printStackTrace();
        }
    }

    private Formatter normalFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder b = new StringBuilder(simpleFormat(record));
                if (record.getThrown() != null)
                    b.append("\n").append(getStackTrace(record.getThrown()));
                return b.append("\n").toString();
            }
        };
    }

    private Formatter ansiFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                var ansi = ansi();
                Level level = record.getLevel();

                if (level == Level.SEVERE) {
                    ansi.fg(RED);
                } else if (level == Level.WARNING) {
                    ansi.fg(YELLOW);
                } else if (level == Level.FINE) {
                    ansi.fg(BLUE);
                } else if (level == Level.FINER) {
                    ansi.fg(CYAN);
                } else if (level == Level.FINEST) {
                    ansi.fg(MAGENTA);
                }

                ansi.a(simpleFormat(record));

                if (record.getThrown() != null)
                    ansi.a("\n").fg(RED).a(getStackTrace(record.getThrown()));
                return ansi.reset().a("\n").toString();
            }
        };
    }

    private String simpleFormat(LogRecord record) {
        long tid = record.getThreadID();
        String thread = tid == LibImpl.MAIN_THREAD_ID ? "main" : String.format("t-%d", tid);
        String prefix = String.format("[%s/%s]", instance.getNameOnly(), thread);
        return String.format("%-12s %tT %s: %s", prefix,
                new Date(record.getMillis()), record.getLevel().getName(), record.getMessage());
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter wr = new StringWriter();
        throwable.printStackTrace(new PrintWriter(wr));
        return wr.toString();
    }
}
