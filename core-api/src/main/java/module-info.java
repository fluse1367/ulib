open module ulib.core.api {
    requires static lombok;
    requires static org.jetbrains.annotations;

    requires java.logging;
    requires java.sql;
    requires org.apache.commons.lang3;

    exports eu.software4you.ulib.core to ulib.core;
    exports eu.software4you.ulib.core.api to ulib.core;
    exports eu.software4you.ulib.core.api.common to ulib.core;
    exports eu.software4you.ulib.core.api.common.collection to ulib.core;
    exports eu.software4you.ulib.core.api.configuration to ulib.core;
    exports eu.software4you.ulib.core.api.configuration.serialization to ulib.core;
    exports eu.software4you.ulib.core.api.configuration.yaml to ulib.core;
    exports eu.software4you.ulib.core.api.database to ulib.core;
    exports eu.software4you.ulib.core.api.database.sql to ulib.core;
    exports eu.software4you.ulib.core.api.database.sql.query to ulib.core;
    exports eu.software4you.ulib.core.api.dependencies to ulib.core;
    exports eu.software4you.ulib.core.api.http to ulib.core;
    exports eu.software4you.ulib.core.api.io to ulib.core;
    exports eu.software4you.ulib.core.api.reflect to ulib.core;
    exports eu.software4you.ulib.core.api.sql to ulib.core;
    exports eu.software4you.ulib.core.api.transform to ulib.core;
    exports eu.software4you.ulib.core.api.utils to ulib.core;
}