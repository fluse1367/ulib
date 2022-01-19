import eu.software4you.ulib.core.api.Lib;
import eu.software4you.ulib.core.api.database.Databases;
import eu.software4you.ulib.core.api.dependencies.Dependencies;
import eu.software4you.ulib.core.api.dependencies.DependencyLoader;
import eu.software4you.ulib.core.api.dependencies.Repositories;
import eu.software4you.ulib.core.api.http.HttpUtil;
import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.core.api.transform.HookInjector;
import eu.software4you.ulib.core.impl.LibImpl;
import eu.software4you.ulib.core.impl.configuration.yaml.YamlSubProvider;
import eu.software4you.ulib.core.impl.database.DatabasesImpl;
import eu.software4you.ulib.core.impl.database.sql.ColumnBuilderProvider;
import eu.software4you.ulib.core.impl.dependencies.DependenciesImpl;
import eu.software4you.ulib.core.impl.dependencies.DependencyLoaderImpl;
import eu.software4you.ulib.core.impl.dependencies.RepositoriesImpl;
import eu.software4you.ulib.core.impl.http.HttpUtilImpl;
import eu.software4you.ulib.core.impl.reflect.ReflectUtilImpl;
import eu.software4you.ulib.core.impl.transform.HookInjectorImpl;

module ulib.core {
    requires static lombok;
    requires static org.jetbrains.annotations;

    requires java.instrument;
    requires java.logging;
    requires java.management;
    requires java.sql;
    requires jdk.attach;

    requires ulib.core.api;
    requires ulib.supermodule;
    requires org.apache.commons.lang3;
    requires org.yaml.snakeyaml;

    // via extra-java-module-info
    requires maven.resolver.provider;
    requires javassist;

    exports eu.software4you.ulib.core.impl to ulib.core.api, ulib.spigot;
    exports eu.software4you.ulib.core.impl.delegation;

    exports eu.software4you.ulib.core.impl.configuration.yaml to ulib.minecraft, ulib.velocity, ulib.spigot;
    exports eu.software4you.ulib.core.impl.configuration to ulib.spigot;

    provides Lib with LibImpl;
    provides Providers.ProviderExtYamlSub with YamlSubProvider;
    provides Databases with DatabasesImpl;
    provides Providers.ProviderColumnBuilder with ColumnBuilderProvider;
    provides Dependencies with DependenciesImpl;
    provides DependencyLoader with DependencyLoaderImpl;
    provides Repositories with RepositoriesImpl;
    provides HttpUtil with HttpUtilImpl;
    provides ReflectUtil with ReflectUtilImpl;
    provides HookInjector with HookInjectorImpl;
}