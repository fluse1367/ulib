# uLib

This library is designed to ease process of developing standalone applications, Velocity, BungeeCord and Spigot plugins.

| [![Gitlab release pipeline status](https://img.shields.io/gitlab/pipeline/software4you.eu/ulib/master?label=Release%20Build&style=for-the-badge)](https://gitlab.com/software4you.eu/ulib/-/pipelines?ref=master) | [![Gitlab snapshot pipeline status](https://img.shields.io/gitlab/pipeline/software4you.eu/ulib/develop?label=Snapshot%20Build&style=for-the-badge)](https://gitlab.com/software4you.eu/ulib/-/pipelines?ref=develop) |
| ---: | ---: |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-core-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-core-api%2Fmaven-metadata.xml) |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=orange&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=orange&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml) |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=yellow&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=yellow&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml) |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=aqua&label=ulib-velocity-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-velocity-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=aqua&label=ulib-velocity-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-velocity-api%2Fmaven-metadata.xml) |

## Disclaimer

This library depends on recent paper/waterfall versions. That means it may not work as expected or may not work at all
on older server versions. You will not receive any support, when using another server version than the one this library
is built for.

If you want to use older server versions, consider a cross-version compatibility tool, like
[ViaBackwards](https://www.spigotmc.org/resources/viabackwards.27448/),
[ViaRewind](https://www.spigotmc.org/resources/viarewind.52109/) or
[ProtocolSupport](https://www.spigotmc.org/resources/protocolsupport.7201/).

When looking up the exact dependencies of uLib, you will notice that it uses [Paper](https://papermc.io/)
and [Waterfall](https://github.com/PaperMC/Waterfall) instead of Spigot and BungeeCord.<br>
Paper/Waterfall provides better performance, and an expanded API which allows uLib to implement more and better
features.<br>
uLib should work on Spigot as well, but some features could not work properly or not work at all as workarounds are
needed to implement some features on Spigot.<br>
Consider using [Purpur](https://purpur.pl3x.net/), [Airplane](https://airplane.gg/)
, [Tuinity](https://github.com/Spottedleaf/Tuinity) or [Paper](https://papermc.io/),
and [Velocity](https://velocitypowered.com/)
or [Watefall](https://github.com/PaperMC/Waterfall) instead of Spigot and BungeeCord.

Also, note the [license of this project](./LICENSE). Use this library at your own risk! The developer(s) / contributors
of this project do not take any responsibility/liability in any way.

## First Startup

When launching uLib for the first time (or if you removed the `.ulib` folder), it will download a few of
dependencies/libraries, this only takes a couple of seconds. The total download size is about 8MB.

Any following start takes about 0.3s.

## Developing with uLib

Before you do anything with uLib, get sure the main class is loaded.

When using the one of the Plugins implementations, you don't have to take care of loading uLib. Only put it in the
respective plugins folder, but don't forget to declare uLib as dependency!

When using standalone implementation you have to load the main class by yourself. There are several ways how to do this.

If you put uLib into your classpath, you can just call `ULib.init();`. Just make sure calling this **before** you do
something with uLib.

Another way is to use the launch function. For this, run uLib with java directly. Supply either the
argument `--launch /path/to/jar/file` (uLib will lookup the main class in the manifest file)
or `--main path.to.MainClass` (here the jar file with this class have to be already in the classpath).

With both options you can also specify arguments that should be passed to the main class, use `:::` as argument
separator:

`--args "--arg:::arg2"`<br>

Your arguments will be passed to your program like this:

(arg0) `--arg`, (arg1) `arg2`

By don't using `:::` (e.g. `--args "--arg arg2"`), your given argument will include a space bar and be passed to your
program like this:

(arg0) `--arg arg2`

All in all, your command could look like this:

```shell
java -jar ulib-core-VERSION-lib.jar --launch my-application.jar --args "--mode:::simple:::--name:::John Doe"
```

or this:

```shell
java -cp ulib-core-VERSION-lib.jar:my-application.jar eu.software4you.ulib.Bootstrap --main my.application.Main --args "--mode:::simple:::--name:::John Doe"
```

## About Java 9+ / Javaagent

If you are using Java 9 or higher and the standalone implementation, **uLib might fail to dynamically load any
dependencies/libraries**. This is due to the restrictions made from Java 9 onwards.

If you still need these functionalities (e.g. the `Dependencies` class), you need to allow uLib to load its so-called
Javaagent.

In order to allow this **on Java 9** (and higher) you need to set the system property `jdk.attach.allowAttachSelf`
to `true` **within the command line**:

```shell
java -Djdk.attach.allowAttachSelf=true -cp ulib-core-VERSION-lib.jar:my-application.jar eu.software4you.ulib.Bootstrap --main my.application.Main --args "--mode:::simple:::--name:::John Doe" 
```

In order for uLib to load the javaagent **on Java 8** though, it needs to load the tools.jar file, that is unfortunately
not available in the regular Java 8 runtime. However, to solve this problem simply make sure the respective JDK is
installed, uLib will try to load the file from there.

## Repository

<details><summary>Gradle</summary>

```groovy
repositories {
    /* ... */
    maven {
        url 'https://repo.software4you.eu/'
        // or url 'https://gitlab.com/api/v4/groups/software4you.eu/-/packages/maven/'
    }
    /* ... */
}
dependencies {
    /* ... */
    compile 'eu.software4you.ulib:ulib-core-api:VERSION'
    compile 'eu.software4you.ulib:ulib-spigot-api:VERSION'
    compile 'eu.software4you.ulib:ulib-bungeecord-api:VERSION'
    compile 'eu.software4you.ulib:ulib-velocity-api:VERSION'
    /* ... */
}
```

</details>
<details><summary>Maven</summary>

```xml

<project>
    <!-- ... -->
    <repositories>
        <!-- ... -->
        <repository>
            <id>software4you-repo</id>
            <url>https://repo.software4you.eu/</url>
            <!-- or <url>https://gitlab.com/api/v4/groups/software4you.eu/-/packages/maven/</url> -->
        </repository>
        <!-- ... -->
    </repositories>
    <dependencies>
        <!-- ... -->
        <dependency>
            <groupId>eu.software4you.ulib</groupId>
            <artifactId>ulib-core-api</artifactId>
            <version>VERSION</version>
        </dependency>

        <dependency>
            <groupId>eu.software4you.ulib</groupId>
            <artifactId>ulib-spigot-api</artifactId>
            <version>VERSION</version>
        </dependency>

        <dependency>
            <groupId>eu.software4you.ulib</groupId>
            <artifactId>ulib-bungeecord-api</artifactId>
            <version>VERSION</version>
        </dependency>

        <dependency>
            <groupId>eu.software4you.ulib</groupId>
            <artifactId>ulib-velocity-api</artifactId>
            <version>VERSION</version>
        </dependency>
        <!-- ... -->
    </dependencies>
    <!-- ... -->
</project>
```

</details>

## Build Instructions

1. **<u>Clone this repository</u>**
   ```shell
   git clone https://gitlab.com/software4you.eu/ulib.git
   ```
2. **`cd` <u>into the directory</u>**
   ```shell
   cd ulib
   ```
3. <details><summary><b><u>Switch to the</u> <code>develop</code> <u>branch</u></b> (Optional)</summary>
   You only need to do this if you want the most recent (unstable) changes.

   ```shell
   git checkout develop
   ```
   </details>


4. **<u>Build it</u>**

   Linux (bash):

   ```shell
   ./gradlew build shadowJar
   ```

   Windows (cmd):

   ```shell
   ./gradlew.bat build shadowJar
   ```

    <details>
        <summary markdown="span">Details</summary>

   -> `build` builds the apis and unready jar files:

    - `ulib-core-VERSION.jar`
    - `ulib-core-api-VERSION.jar`
    - `ulib-velocity-VERSION.jar`
    - `ulib-velocity-api-VERSION.jar`
    - `ulib-bungeecord-VERSION.jar`
    - `ulib-bungeecord-api-VERSION.jar`
    - `ulib-spigot-VERSION.jar`
    - `ulib-spigot-api-VERSION.jar`

   -> `shadowJar` builds the ready-for-use jar files:

    - `ulib-core-VERSION-lib.jar`
    - `ulib-velocity-VERSION-plugin.jar`
    - `ulib-bungeecord-VERSION-plugin.jar`
    - `ulib-spigot-VERSION-plugin.jar`

    </details>


5. <details><summary><b><u>Build the javadocs webpage</u></b> (Optional)</summary>

   Building the javadocs webpage is probably more interesting for developers who are using the development
   snapshots  (`develop` branch) of ulib, because the javadocs of them won't get published.

   Linux (bash):

    ```shell
    ./gradlew docsWebpage
    ```

   Windows (cmd):

    ```shell
    ./gradlew.bat docsWebpage
    ```

   You'll find the webpage in the directory `public`.

</details>

# Included Software

The following 3rd-party software is included within this project:

- NBTEditor ([click](https://github.com/BananaPuncher714/NBTEditor/tree/4884d2f95f2e648de6db12c0a1dcaaae2d866cef))
  Copyright (c) 2018 [BananaPuncher714](https://github.com/BananaPuncher714), licensed under
  the [MIT license](https://raw.githubusercontent.com/BananaPuncher714/NBTEditor/4884d2f95f2e648de6db12c0a1dcaaae2d866cef/LICENSE)
- Bukkit (Copyright (c) 2020 Bukkit, licensed under
  the [GNU General Public License v3.0](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/raw/LICENCE.txt?at=85e683b7eb8d14911ce47d309558caf3a968bde7)):
    - YAML-Configuration
      Package ([click](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/configuration?at=85e683b7eb8d14911ce47d309558caf3a968bde7))
    - NumberConversions
      Class ([click](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/util/NumberConversions.java?at=85e683b7eb8d14911ce47d309558caf3a968bde7))
- ParticleEffect
  Library ([click](https://github.com/DarkBlade12/ParticleEffect/tree/df3f57fa3f1ecd82ad8efac24dcf8371b993c019))
  Copyright (c) 2016 [DarkBlade12](https://github.com/DarkBlade12), custom license (see
  [here](https://gitlab.com/software4you.eu/ulib/-/blob/master/ulib-spigot-api/src/main/java/eu/software4you/minecraft/multiversion/BukkitReflectionUtils.java)
  and [here](https://gitlab.com/software4you.eu/ulib/-/blob/master/ulib-spigot-api/src/main/java/eu/software4you/minecraft/multiversion/ParticleEffect.java))

The following 3rd-party software is not included within the source code of this project, but within the shadowed ("
ready-for-use") build artifacts:

- [Apache Commons](https://commons.apache.org/) (Copyright (c)
  2021 [The Apache Software Foundation](https://github.com/apache), licensed under
  the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)):
    - IO ([click](https://github.com/apache/commons-io)) version 2.8
    - Configuration ([click](https://github.com/apache/commons-configuration/)) version 2.4
    - Lang ([click](https://github.com/apache/commons-lang/)) version 3.8.1 & 2.6
    - BeanUtils ([click](https://github.com/apache/commons-beanutils/)) version 1.9.3
    - Codec ([click](https://github.com/apache/commons-codec/)) version 1.11
- HttpComponents HttpClient ([click](https://github.com/apache/httpcomponents-client)) Copyright (c)
  2020 [The Apache Software Foundation](https://github.com/apache), licensed under
  the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) version 4.5.10
- [Maven Artifact Resolver](https://github.com/apache/maven-resolver) (Copyright (c)
  2021 [The Apache Software Foundation](https://github.com/apache), licensed under
  the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)):
    - Provider ([click](https://github.com/apache/maven/tree/master/maven-resolver-provider)) version 3.8.1
    - Connector Basic ([click](https://github.com/apache/maven-resolver/tree/master/maven-resolver-connector-basic))
      version 1.6.2
    - Transport HTTP ([click](https://github.com/apache/maven-resolver/tree/master/maven-resolver-transport-http)):
      version 1.6.2
- Guava ([click](https://github.com/google/guava)) Copyright (c) 2018 [Google](https://github.com/google), licensed
  under the [Apache License 2.0](https://github.com/google/guava/blob/master/COPYING), version 27.0.1
- Gson ([click](https://github.com/google/gson)) Copyright (c) 2019 [Google](https://github.com/google), licensed under
  the [Apache License 2.0](https://github.com/google/gson/blob/master/LICENSE), version 27.0.1
- SnakeYAML ([click](https://github.com/asomov/snakeyaml)) Copyright (c) 2018 [Andrey Somov](https://github.com/asomov),
  licensed under the [Apache License 2.0](https://github.com/asomov/snakeyaml/blob/master/LICENSE.txt), version 1.23
- Jansi ([click](https://github.com/fusesource/jansi)) Copyright (c) 2019 [FuseSource](https://github.com/fusesource),
  licensed under the [Apache License 2.0](https://github.com/fusesource/jansi/blob/master/license.txt), version 1.18
- Jackson Core ([click](https://github.com/FasterXML/jackson-core)) Copyright (c)
  2018 [FasterXML](https://github.com/FasterXML), licensed under
  the [Apache License 2.0](https://github.com/FasterXML/jackson-core/blob/2.13/LICENSE), version 2.9.8
- Jackson Databind ([click](https://github.com/FasterXML/jackson-databind)) Copyright (c)
  2018 [FasterXML](https://github.com/FasterXML), licensed under
  the [Apache License 2.0](https://github.com/FasterXML/jackson-core/blob/2.13/LICENSE), version 2.9.8
- Javassist ([click](https://github.com/jboss-javassist/javassist)) Copyright (c)
  2020 [Shigeru Chiba](https://github.com/jboss-javassist), custom license
  (click [here](https://github.com/jboss-javassist/javassist/blob/master/License.html)), version 3.27.0-GA 