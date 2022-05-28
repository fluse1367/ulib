[<- Back to Overview](Readme.md)

## uLib System Properties

---

You can specify certain settings in uLib with the java system properties.

For instance, you can set a property using the `-D` argument in the java command. As it says in the `java -help`
command:

```shell
...@...:~$ java -help
    ...
    -D<name>=<value>
                  set a system property
    ...
```

Setting a system property (or multiple) would look like this:

```shell
java -Dsystem.property.key=my_value ...
```

The following settings are changeable with the system properties:

| Property                     | Default       | Description                                                                                                             |
|:-----------------------------|---------------|:------------------------------------------------------------------------------------------------------------------------|
| `ulib.directory.main`        | "`.ulib`"     | The main data-directory.                                                                                                |
| `ulib.directory.cache`       | "`cache`"     | Directory where cached files will be placed. By default it will be placed inside the data-directory.                    |
| `ulib.directory.libraries`   | "`libraries`" | Directory where library files will be placed. By default it will be placed inside the data-directory.                   |
| `ulib.install.module_layer`  | "`parent`"    | Specifies the loader behavior. Possible values: `boot`, `comply`, `parent`.                                             |
| `ulib.install.agent_trigger` | -             | Enables the self-initialization or self-installation of ulib on java agent init. Possible values: `init`, `selfinstall` |

The properties have to be set before starting java/ulib, changing them during runtime takes no effect.