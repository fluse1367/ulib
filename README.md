# uLib

This library is designed to ease process of developing standalone applications, Velocity, BungeeCord and Spigot plugins.

Copyright (c) 2021 [fluse1367](https://gitlab.com/fluse1367) / [software4you.eu](https://gitlab.com/software4you.eu)   
See "Included Software" (at the bottom) for copyright and license notice of included software.

|                                                                                                                                                                                                                                                        RELEASE BUILD |                                                                                                                                                                                                                                                       SNAPSHOT BUILD |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|                   ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-loader&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-loader%2Fmaven-metadata.xml) |                   ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-loader&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-loader%2Fmaven-metadata.xml) |
|               ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-core-api%2Fmaven-metadata.xml) |               ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-core-api%2Fmaven-metadata.xml) |
|         ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=orange&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml) |         ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=orange&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml) |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=yellow&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=yellow&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml) |
|       ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=aqua&label=ulib-velocity-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-velocity-api%2Fmaven-metadata.xml) |       ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=aqua&label=ulib-velocity-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-velocity-api%2Fmaven-metadata.xml) |

## Important Things to Know

- This library depends on recent paper/waterfall versions. That means it may not work as expected or may not work at all
  on older server versions. You will not receive any support, when using another server version than the one this
  library is built for. <br><br>
  If you want to use older server versions, consider a cross-version compatibility tool, like
  [ViaBackwards](https://www.spigotmc.org/resources/viabackwards.27448),
  [ViaRewind](https://www.spigotmc.org/resources/viarewind.52109) or
  [ProtocolSupport](https://www.spigotmc.org/resources/protocolsupport.7201).


- Minimum Java version is 17.


- When launching uLib for the first time (or if the respective caching folder was removed), it will download a few of
  dependencies/libraries.

### Disclaimer

Note the copyright and [license of this project](./LICENSE). Use this library at your own risk! The contributors of this
project do not take any responsibility/liability in any way.


---

# Developing with uLib

## Repository

See the versions table to find out the most recent versions.  
Make sure you only include the `loader` as runtime library.

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
    implementation 'eu.software4you.ulib:ulib-loader:VERSION'
    compileOnly 'eu.software4you.ulib:ulib-core-api:VERSION'
    compileOnly 'eu.software4you.ulib:ulib-spigot-api:VERSION'
    compileOnly 'eu.software4you.ulib:ulib-bungeecord-api:VERSION'
    compileOnly 'eu.software4you.ulib:ulib-velocity-api:VERSION'
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
            <artifactId>ulib-loader</artifactId>
            <version>VERSION</version>
            <scope>provided</scope>
        </dependency>

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

## Loading uLib into the Runtime

Before you do anything with uLib, get sure you load the library with its loader.

When using the one of the Plugins implementations, you don't have to take care of loading it; It's enough to put the
loader in the respective `plugins` folder, but don't forget to declare uLib as dependency!

When using the standalone implementation, you have to load the library class by yourself. There are several ways how to
do this.

If you put uLib into your classpath, you can use the `Installer` class from the loader and load uLib into your current
class loader:

```java
// eu.software4you.ulib.loader.install.Installer
Installer.installTo(getClass().getClassLoader());
```

Also, if you are installing it into a modular context, you have to add a `reads` record to that module before using
uLib. Otherwise, your module won't have access to the uLib API. This example shows how it can be done easily:

```java
getClass().getModule().addReads(Installer.getModule());
```

Make sure installing uLib properly into the runtime **before** you do _anything_ with uLib (this includes loading one of
ulib's classes!).

### Alternatives

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

## Troubleshooting

Because ulib uses complex mechanics to inject itself into your desired class loader context, it is fairly easy for it to
fail. Analyzing and understanding what went wrong can be pretty tough. Common malfunctions and possible fixes listed are
listed below.

- ```
  Module ulib.core.api not found, required by mymodule
  ```
  Because uLib is loaded by the installer **after** the initialization of the boot layer, the uLib API module is not
  available at the time of initialization. Change the `requires ulib.core.api;` record in your module info file
  to `requires static`.
- ```
  class myclass (in module mymodule) cannot access class ulibclass (in module ulib.core.api) ...
  ```
  Because the `reads` record in your module info file is declared as static, you must add a `reads` record to your
  module manually before you can access the uLib API: `getClass().getModule().addReads(Installer.getModule());`
- ```
  Module some-module reads more than one module named other-module
  ```
  Some of uLib's dependencies are already loaded by a higher module layer of your runtime. Try to add the java startup
  flag `-Dulib.install.module_layer=boot`. If that doesn't work try `-Dulib.install.module_layer=comply`.

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
- Apache Commons Lang v3 (Copyright (c) 2022 [The Apache Software Foundation](https://www.apache.org/), licensed under
  the [Apache License 2.0](https://github.com/apache/commons-lang/blob/9f1ac974e1b52c58b1950acdaaf3e0d5881409df/LICENSE.txt)):
    - SystemUtils
      Class ([click](https://github.com/apache/commons-lang/blob/9f1ac974e1b52c58b1950acdaaf3e0d5881409df/src/main/java/org/apache/commons/lang3/SystemUtils.java))
    - JavaVersion
      Class ([click](https://github.com/apache/commons-lang/blob/9f1ac974e1b52c58b1950acdaaf3e0d5881409df/src/main/java/org/apache/commons/lang3/JavaVersion.java))

---