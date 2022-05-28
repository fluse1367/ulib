[<- Back to Overview](Readme.md)

# uLib Developer Reference

---

## Contents

1. [Build from Source](#build-from-source)
2. [Maven Repository](#maven-repository)
3. [Loading/Initializing uLib](#loading-ulib-into-the-runtime)
4. [The Javaagent](#about-the-javaagent)
5. [Troubleshooting](#troubleshooting)

---

## Build from Source

Building ulib from source is super easy. Clone the repository and build it with gradle.

Note: You need to have the JDK 17 installed.

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

## Maven Repository

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

---

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
//or
        Installer.installMe();
```

Also, if you are installing it into a modular context, you may have to add a `reads` record to that module before using
uLib. Otherwise, your module won't have access to the uLib API. When using `Installer.installMe()` this is automatically
done for you.

This example shows how it can be done easily:

```java
getClass().getModule().addReads(Installer.getLayer().findModule("ulib.core").get());
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

Please also refer to the [user troubleshooting guide](Troubleshooting.md).

---