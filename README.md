# uLib
uLib is a library designed to ease process of developing standalone applications, BungeeCord or Spigot plugins.

You can lookup the current version in the `build.gradle` file.
### First Startup
When launching uLib for the first time (or if you removed the libs folder), it will download a bunch of dependencies/libraries.
This usually takes about 40-60s. To total download size is about 35-40 MB.

Any following start takes about 0.5s.
### Developing with uLib
Before you do anything with uLib, get sure the main class is loaded.

When using the Spigot or BungeeCord implementation, you don't have to take care of loading uLib.
Only put it in the Spigot/BungeeCord plugins folder.
Don't forget to supply uLib as (soft-)dependency in your `plugin.yml`/`bungee.yml`!

When using standalone implementation you have to load the main class by yourself.
There are several ways how to do this.

If you put uLib into your classpath, you can just call `ULib.makeReady();`.
Just make sure calling this **before** you do _anything_ with uLib.

Another way is to use the launch function. For this, run uLib with java directly.
Supply either the argument `--launch /path/to/jar/file` (uLib will lookup the main class in the manifest file) or `--main path.to.MainClass` (here the jar file with this class have to be already in the classpath).

With both options you can also specify arguments that should be passed to the main class, use `:::` as argument separator:

`--args "--arg:::arg2"`<br>

Your arguments will be passed to your program like this:

(arg0) `--arg`, (arg1) `arg2`

By don't using `:::` (e.g. `--args "--arg arg2"`), your given argument will include a space bar and be passed to your program like this:

(arg0) `--arg arg2`

All in all, your command could look like this:

`java -jar ulib-core-X.X.X.jar --launch my-application.jar --args "--mode:::simple:::--name:::John Doe"`<br>
or this:<br>
`java -cp ulib-core-X.X.X.jar:my-application.jar eu.software4you.ulib.Bootstrap --main my.application.Main --args "--mode:::simple:::--name:::John Doe"`
#### Gradle Setup
```groovy
repositories {
    ...
    maven { url 'https://repo.software4you.eu/' }
    ...
}
dependencies {
    ...
    compile 'eu.software4you.ulib:ulib-core:VERSION'
    compileOnly 'eu.software4you.ulib:ulib-spigot-api:VERSION'
    compileOnly 'eu.software4you.ulib:ulib-bungeecord-api:VERSION'
    ...
}
```
#### Maven Setup
```
<repositories>
    ...
    <repository>
        <id>software4you-repo</id>
        <url>https://repo.software4you.eu/</url>
    </repository>
    ...
</repositories>
<dependencies>
    ...
    <dependency>
        <groupId>eu.software4you.ulib</groupId>
        <artifactId>ulib-core</artifactId>
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
    ...
</dependencies>
```