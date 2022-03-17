module ulib.core {

    requires static lombok;
    requires static org.jetbrains.annotations;

    // java
    requires java.instrument;
    requires java.sql;

    // 3rd party
    requires javassist;
    requires org.yaml.snakeyaml;


    // TODO: exports
}