module ulib.core {
    // static
    requires static lombok;
    requires static org.jetbrains.annotations;

    // java
    requires java.instrument;
    requires java.sql;

    // 3rd party
    requires javassist;
    requires org.yaml.snakeyaml;
    requires json.simple;
    requires maven.resolver.provider;

    // api exports
    exports eu.software4you.ulib.core.collection;
    exports eu.software4you.ulib.core.common;
    exports eu.software4you.ulib.core.configuration;
    exports eu.software4you.ulib.core.configuration.serialization;
    exports eu.software4you.ulib.core.database;
    exports eu.software4you.ulib.core.database.exception;
    exports eu.software4you.ulib.core.database.sql;
    exports eu.software4you.ulib.core.database.sql.query;
    exports eu.software4you.ulib.core.dependencies;
    exports eu.software4you.ulib.core.function;
    exports eu.software4you.ulib.core.http;
    exports eu.software4you.ulib.core.inject;
    exports eu.software4you.ulib.core.io;
    exports eu.software4you.ulib.core.reflect;
    exports eu.software4you.ulib.core.util;

    // init export
    exports eu.software4you.ulib.core.impl.init;
}