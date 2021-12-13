module ulib.core {
    requires static lombok;

    requires java.instrument;
    requires java.logging;
    requires java.management;
    requires java.sql;
    requires jdk.attach;

    requires ulib.core.api;
    requires org.apache.commons.lang3;
    requires org.jetbrains.annotations;
}