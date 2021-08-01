# uLib

This library is designed to ease process of developing standalone applications, Velocity, BungeeCord and Spigot plugins.

Copyright (c) 2021 [fluse1367](https://gitlab.com/fluse1367) / [software4you.eu](https://gitlab.com/software4you.eu)   
See "Included Software" (at the bottom) for copyright and license notice of included software.

| [![Gitlab release pipeline status](https://img.shields.io/gitlab/pipeline/software4you.eu/ulib/master?label=Release%20Build&style=for-the-badge)](https://gitlab.com/software4you.eu/ulib/-/pipelines?ref=master) | [![Gitlab snapshot pipeline status](https://img.shields.io/gitlab/pipeline/software4you.eu/ulib/develop?label=Snapshot%20Build&style=for-the-badge)](https://gitlab.com/software4you.eu/ulib/-/pipelines?ref=develop) |
| ---: | ---: |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-core-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-core-api%2Fmaven-metadata.xml) |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=orange&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=orange&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml) |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=yellow&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=yellow&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml) |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=aqua&label=ulib-velocity-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-velocity-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=aqua&label=ulib-velocity-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-velocity-api%2Fmaven-metadata.xml) |

## Important Things to Know

- This library depends on recent paper/waterfall versions. That means it may not work as expected or may not work at all
  on older server versions. You will not receive any support, when using another server version than the one this
  library is built for. <br><br>
  If you want to use older server versions, consider a cross-version compatibility tool, like
  [ViaBackwards](https://www.spigotmc.org/resources/viabackwards.27448),
  [ViaRewind](https://www.spigotmc.org/resources/viarewind.52109) or
  [ProtocolSupport](https://www.spigotmc.org/resources/protocolsupport.7201).


- Minimum Java version is 16.


- When looking up the exact dependencies of uLib, you will notice that it uses [Paper](https://papermc.io)
  and [Waterfall](https://github.com/PaperMC/Waterfall) instead of Spigot and BungeeCord.   
  Paper/Waterfall provide better performance, and an expanded API which allows uLib to implement more and better
  features.   
  uLib should work on Spigot as well, but some features could not work properly or not work at all as workarounds are
  necessary to implement some features on Spigot. <br><br>
  Consider using [Purpur](https://purpur.pl3x.net), [Airplane](https://airplane.gg),
  [Tuinity](https://github.com/Spottedleaf/Tuinity) or [Paper](https://papermc.io),
  and [Velocity](https://velocitypowered.com)
  or [Watefall](https://github.com/PaperMC/Waterfall) instead of Spigot and BungeeCord.


- When launching uLib for the first time (or if the `libraries`/`cache` folder was removed), it will download a few of
  dependencies/libraries (about 2 MB).

### Disclaimer

Also, note the copyright and [license of this project](./LICENSE). Use this library at your own risk! The developer(s) /
contributors of this project do not take any responsibility/liability in any way.


---

# Developing with uLib

## Repository

See the versions table to find out the most recent versions.

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

## Main Entry Class

Before you do anything with uLib, get sure the main class is loaded.

When using the one of the Plugins implementations, you don't have to take care of loading uLib. Only put it in the
respective `plugins` folder, but don't forget to declare uLib as dependency!

When using the standalone implementation, you have to load the main class by yourself. There are several ways how to do
this.

If you put uLib into your classpath, you can just call `ULib.init();`. Just make sure calling this **before** you do
something with uLib.

Another way is to use the launch function. For this, run uLib with java directly. Supply either the
argument `--launch /path/to/jar/file` (uLib will lookup the main class in the manifest file)
or `--main path.to.MainClass` (here the jar file with this class have to be already in the classpath).

With both options you can also specify arguments that should be passed to the main class, use `:::` as argument
separator:

`--args "--arg:::arg2"`

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

---

# Build Instructions

1. **Clone this repository**
   ```shell
   git clone https://gitlab.com/software4you.eu/ulib.git
   ```
2. **`cd` into the directory**
   ```shell
   cd ulib
   ```
3. <details><summary><b>Switch to the <code>develop</code> branch</b> (Optional)</summary>
   You only need to do this if you want the most recent (unstable) changes.

   ```shell
   git checkout develop
   ```
   </details>


4. **Build it**

   Linux (bash):

   ```shell
   ./gradlew build shadowJar
   ```

   Windows (cmd):

   ```shell
   ./gradlew.bat build shadowJar
   ```

    <details><summary>Details</summary>

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


5. <details><summary><b>Build the javadocs webpage</b> (Optional)</summary>

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

---

# Included Software

The following 3rd-party software is included within this project:

- NBTEditor ([click](https://github.com/BananaPuncher714/NBTEditor/tree/62e8919f10415aaff73f86aa8d4561f2ec4de791))
  Copyright (c) 2018 [BananaPuncher714](https://github.com/BananaPuncher714), licensed under
  the [MIT license](https://github.com/BananaPuncher714/NBTEditor/blob/62e8919f10415aaff73f86aa8d4561f2ec4de791/LICENSE)
- ParticleEffect
  Library ([click](https://github.com/DarkBlade12/ParticleEffect/tree/df3f57fa3f1ecd82ad8efac24dcf8371b993c019))
  Copyright (c) 2016 [DarkBlade12](https://github.com/DarkBlade12), custom license (see
  [here](https://gitlab.com/software4you.eu/ulib/-/blob/master/ulib-spigot-api/src/main/java/eu/software4you/minecraft/multiversion/BukkitReflectionUtils.java)
  and [here](https://gitlab.com/software4you.eu/ulib/-/blob/master/ulib-spigot-api/src/main/java/eu/software4you/minecraft/multiversion/ParticleEffect.java))
- CloudNet v3 (Copyright (c) 2019 [CloudNetService](https://github.com/CloudNetService), licensed under
  the [Apache License 2.0](https://github.com/CloudNetService/CloudNet-v3/blob/2fcc7b6e3bd0d8120effce2cf349eea4ee3a595d/LICENSE)):
    - Pair
      Class ([click](https://github.com/CloudNetService/CloudNet-v3/blob/2fcc7b6e3bd0d8120effce2cf349eea4ee3a595d/cloudnet-common/src/main/java/de/dytanic/cloudnet/common/collection/Pair.java))
    - Triple
      Class ([click](https://github.com/CloudNetService/CloudNet-v3/blob/2fcc7b6e3bd0d8120effce2cf349eea4ee3a595d/cloudnet-common/src/main/java/de/dytanic/cloudnet/common/collection/Triple.java))

The following 3rd-party software is not included within the source code of this project, but redistributed within the
shadowed build artifacts:

- Commons Lang ([click](https://github.com/apache/commons-lang/tree/LANG_3_8_1)) (Copyright (c)
  2021 [The Apache Software Foundation](https://github.com/apache), licensed under
  the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)), version 3.8.1
- [Maven Artifact Resolver](https://github.com/apache/maven-resolver/tree/maven-resolver-1.6.2) (Copyright (c)
  2021 [The Apache Software Foundation](https://github.com/apache), licensed under
  the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)):
    - Provider ([click](https://github.com/apache/maven/tree/maven-3.8.1/maven-resolver-provider)) version 3.8.1
    - Connector
      Basic ([click](https://github.com/apache/maven-resolver/tree/maven-resolver-1.6.2/maven-resolver-connector-basic))
      version 1.6.2
    - Transport
      HTTP ([click](https://github.com/apache/maven-resolver/tree/maven-resolver-1.6.2/maven-resolver-transport-http))
      version 1.6.2
- Guava ([click](https://github.com/google/guava/tree/v27.0.1)) Copyright (c) 2018 [Google](https://github.com/google),
  licensed under the [Apache License 2.0](https://github.com/google/guava/blob/v27.0.1/COPYING), version 27.0.1
- Gson ([click](https://github.com/google/gson/tree/gson-parent-2.8.6)) Copyright (c)
  2019 [Google](https://github.com/google), licensed under
  the [Apache License 2.0](https://github.com/google/gson/blob/gson-parent-2.8.6/LICENSE), version 2.8.6
- SLF4J-Simple ([click](https://github.com/qos-ch/slf4j/tree/v_1.7.32)) Copyright (c)
  2019 [QOS](https://github.com/qos-ch), licensed under the
  [MIT license](https://github.com/qos-ch/slf4j/blob/v_1.7.32/LICENSE.txt), version 1.7.32
- SnakeYAML ([click](https://github.com/asomov/snakeyaml/tree/b28f0b4d87c60ef4dd2aed9188a4c7f7fbb0ae66)) Copyright (c)
  2018 [Andrey Somov](https://github.com/asomov), licensed under the
  [Apache License 2.0](https://github.com/asomov/snakeyaml/blob/b28f0b4d87c60ef4dd2aed9188a4c7f7fbb0ae66/LICENSE.txt),
  version 1.28
- Jansi ([click](https://github.com/fusesource/jansi/tree/jansi-project-1.18)) Copyright (c)
  2019 [FuseSource](https://github.com/fusesource), licensed under
  the [Apache License 2.0](https://github.com/fusesource/jansi/blob/jansi-project-1.18/license.txt), version 1.18
- Javassist ([click](https://github.com/jboss-javassist/javassist/tree/rel_3_27_0_ga)) Copyright (c)
  2020 [Shigeru Chiba](https://github.com/jboss-javassist), used under the
  the [Apache License 2.0](https://github.com/jboss-javassist/javassist/blob/rel_3_27_0_ga/License.html) (see bottom),
  version 3.27.0-GA

---