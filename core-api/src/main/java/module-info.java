open module ulib.core.api {
    requires static lombok;
    requires static org.jetbrains.annotations;

    requires java.logging;
    requires java.sql;
    requires org.apache.httpcomponents.httpclient;

    exports eu.software4you.ulib.core;
    exports eu.software4you.ulib.core.api;
    exports eu.software4you.ulib.core.api.common;
    exports eu.software4you.ulib.core.api.common.collection;
    exports eu.software4you.ulib.core.api.configuration;
    exports eu.software4you.ulib.core.api.configuration.serialization;
    exports eu.software4you.ulib.core.api.configuration.yaml;
    exports eu.software4you.ulib.core.api.database;
    exports eu.software4you.ulib.core.api.database.sql;
    exports eu.software4you.ulib.core.api.database.sql.query;
    exports eu.software4you.ulib.core.api.dependencies;
    exports eu.software4you.ulib.core.api.http;
    exports eu.software4you.ulib.core.api.io;
    exports eu.software4you.ulib.core.api.reflect;
    exports eu.software4you.ulib.core.api.sql;
    exports eu.software4you.ulib.core.api.transform;
    exports eu.software4you.ulib.core.api.utils;

    uses eu.software4you.ulib.core.api.Lib;
    uses eu.software4you.ulib.core.api.configuration.yaml.YamlSub.Provider;
    uses eu.software4you.ulib.core.api.database.Databases;
    uses eu.software4you.ulib.core.api.database.sql.ColumnBuilder.Provider;
    uses eu.software4you.ulib.core.api.dependencies.Dependencies;
    uses eu.software4you.ulib.core.api.dependencies.DependencyLoader;
    uses eu.software4you.ulib.core.api.dependencies.Repositories;
    uses eu.software4you.ulib.core.api.http.HttpUtil;
    uses eu.software4you.ulib.core.api.reflect.ReflectUtil;
    uses eu.software4you.ulib.core.api.transform.HookInjector;
}