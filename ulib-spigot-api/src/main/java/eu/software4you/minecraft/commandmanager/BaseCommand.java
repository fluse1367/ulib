package eu.software4you.minecraft.commandmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BaseCommand {
    Sender sender();

    String command();

    String[] commandAliases() default {};

    String permission() default "";

    String subCommand() default "";

    boolean aSync() default false;

    enum Sender {
        PLAYER,
        CONSOLE
    }
}
