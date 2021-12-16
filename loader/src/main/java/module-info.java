module ulib.loader {
    requires static lombok;

    requires static ulib.core.api;
    requires static ulib.core;
    requires static joptsimple;

    requires java.instrument;
    requires java.logging;
    requires jdk.attach;

    opens eu.software4you.ulib.loader.install;
    exports eu.software4you.ulib.loader.agent to java.instrument;
}