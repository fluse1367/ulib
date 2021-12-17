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

    // via extra-java-module-info
    requires maven.resolver.provider;
    requires javassist;

    exports eu.software4you.ulib.core.impl to ulib.loader, ulib.core.api;
    exports eu.software4you.ulib.core.impl.dependencies to ulib.loader;

    provides eu.software4you.ulib.core.api.Lib with eu.software4you.ulib.core.impl.LibImpl;
    provides eu.software4you.ulib.core.api.configuration.yaml.YamlSub.Provider with eu.software4you.ulib.core.impl.configuration.yaml.YamlSubProvider;
    provides eu.software4you.ulib.core.api.database.Databases with eu.software4you.ulib.core.impl.database.DatabasesImpl;
    provides eu.software4you.ulib.core.api.database.sql.ColumnBuilder.Provider with eu.software4you.ulib.core.impl.database.sql.ColumnBuilderProvider;
    provides eu.software4you.ulib.core.api.dependencies.Dependencies with eu.software4you.ulib.core.impl.dependencies.DependenciesImpl;
    provides eu.software4you.ulib.core.api.dependencies.DependencyLoader with eu.software4you.ulib.core.impl.dependencies.DependencyLoaderImpl;
    provides eu.software4you.ulib.core.api.dependencies.Repositories with eu.software4you.ulib.core.impl.dependencies.RepositoriesImpl;
    provides eu.software4you.ulib.core.api.http.HttpUtil with eu.software4you.ulib.core.impl.http.HttpUtilImpl;
    provides eu.software4you.ulib.core.api.reflect.ReflectUtil with eu.software4you.ulib.core.impl.reflect.ReflectUtilImpl;
    provides eu.software4you.ulib.core.api.transform.HookInjector with eu.software4you.ulib.core.impl.transform.HookInjectorImpl;
}