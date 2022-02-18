import eu.software4you.ulib.core.api.Lib;
import eu.software4you.ulib.core.api.database.Databases;
import eu.software4you.ulib.core.api.dependencies.Dependencies;
import eu.software4you.ulib.core.api.dependencies.DependencyLoader;
import eu.software4you.ulib.core.api.dependencies.Repositories;
import eu.software4you.ulib.core.api.http.HttpUtil;
import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.core.api.transform.HookInjector;

module ulib.core.api {
    requires static lombok;
    requires static org.jetbrains.annotations;

    requires java.logging;
    requires java.sql;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    exports eu.software4you.ulib.core.api.common.collection;
    exports eu.software4you.ulib.core.api.common.validation;
    exports eu.software4you.ulib.core.api.common;
    exports eu.software4you.ulib.core.api.configuration.serialization;
    exports eu.software4you.ulib.core.api.configuration.yaml;
    exports eu.software4you.ulib.core.api.configuration;
    exports eu.software4you.ulib.core.api.database.exception;
    exports eu.software4you.ulib.core.api.database.sql.query;
    exports eu.software4you.ulib.core.api.database.sql;
    exports eu.software4you.ulib.core.api.database;
    exports eu.software4you.ulib.core.api.dependencies;
    exports eu.software4you.ulib.core.api.function;
    exports eu.software4you.ulib.core.api.http;
    exports eu.software4you.ulib.core.api.io;
    exports eu.software4you.ulib.core.api.math;
    exports eu.software4you.ulib.core.api.reflect;
    exports eu.software4you.ulib.core.api.sql;
    exports eu.software4you.ulib.core.api.transform;
    exports eu.software4you.ulib.core.api.util.value;
    exports eu.software4you.ulib.core.api.util;
    exports eu.software4you.ulib.core.api;
    exports eu.software4you.ulib.core;

    exports eu.software4you.ulib.core.api.internal to
            ulib.core, ulib.minecraft.api, ulib.bungeecord.api, ulib.velocity.api, ulib.spigot.api;

    uses Lib;
    uses Providers.ProviderExtYamlSub;
    uses Databases;
    uses Providers.ProviderColumnBuilder;
    uses Dependencies;
    uses DependencyLoader;
    uses Repositories;
    uses HttpUtil;
    uses ReflectUtil;
    uses HookInjector;
}