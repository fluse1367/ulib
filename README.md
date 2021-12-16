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


- Minimum Java version is 17.


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


- When launching uLib for the first time (or if the respective caching folder was removed), it will download a few of
  dependencies/libraries.

### Disclaimer

Note the copyright and [license of this project](./LICENSE). Use this library at your own risk! The contributors of this
project do not take any responsibility/liability in any way.


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

## The uLib-loader

Before you do anything with uLib, get sure you load the library with its loader.

When using the one of the Plugins implementations, you don't have to take care of loading it; It's enough to put the
loader in the respective `plugins` folder, but don't forget to declare uLib as dependency!

When using the standalone implementation, you have to load the library class by yourself. There are several ways how to
do this.

If you put uLib into your classpath, you can simply load the `Installer` class from the loader:

```java
Class.forName("eu.software4you.ulib.loader.install.Installer");
```

Just make sure loading the installer **before** you do _anything_ with uLib (this includes loading one of ulib's
classes!).

Another way is to use the launch function. For this, run the loader directly. Supply either the
argument `--launch /path/to/application.jar` (the loader will look up the main class in the manifest file)
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
java -jar ulib-loader-VERSION.jar --launch my-application.jar --args "--mode:::simple:::--name:::John Doe"
```

or this:

```shell
java -cp ulib-loader-VERSION.jar:my-application.jar eu.software4you.ulib.loader.launch.Main --main my.application.Main --args "--mode:::simple:::--name:::John Doe"
```

## About the Javaagent

ULib realizes several things utilizing a so-called Javaagent. This agent is **crucial** for the library to run.  
In fact, the loader even depends on it, to properly load it. **Without this agent, uLib will fail in every extend.**

By default, the loader uses a workaround method to self-initialize the Javaagent, however this should be avoided it
possible.

The best solution is to supply the loader as javaagent to the JVM (with an additional flag):

```shell
java -javaagent:path/to/ulib-loader.jar ...
```

If the solution above does not work for you, another thing you can try is to allow the application to self-attach a
javaagent (again, with an additional flag):

```shell
java -Djdk.attach.allowAttachSelf=true ... 
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
3. <details><summary><b>Switch to another branch</b> (Optional)</summary>

   ```shell
   git checkout BRANCH_NAME
   ```
   </details>


4. **Build it**

   Linux (bash):

   ```shell
   ./gradlew build
   ```

   Windows (cmd):

   ```shell
   ./gradlew.bat build
   ```

   You will find the loader in `loader/build/libs/`.


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

---