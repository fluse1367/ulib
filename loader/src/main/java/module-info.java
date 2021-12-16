module ulib.loader {
    requires static lombok;

    requires java.instrument;
    requires java.logging;
    requires jdk.attach;

    requires ulib.core.api;
    requires ulib.core;
    requires joptsimple;

    opens eu.software4you.ulib.loader.install;
}