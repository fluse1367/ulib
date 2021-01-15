# uLib <br>[![pipeline status](https://img.shields.io/gitlab/pipeline/software4you.eu/ulib/master)](https://gitlab.com/software4you.eu/ulib/-/commits/master) [![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core&metadataUrl=https%3A%2F%2Frepo.software4you.eu%2Feu%2Fsoftware4you%2Fulib%2Fulib-core%2Fmaven-metadata.xml)](README.md#maven-setup) [![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Frepo.software4you.eu%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml)](README.md#maven-setup) [![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Frepo.software4you.eu%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml)](README.md#maven-setup)
uLib is a library designed to ease process of developing standalone applications, BungeeCord or Spigot plugins.

### Disclaimer
This library depends on recent paper/waterfall versions.
That means it may not work as expected or may not work at all on older server versions.
You will not receive any support, when using another server version than the one this library is built for.

If you want to use older server versions, consider a cross-version compatibility tool, like
[ViaBackwards](https://www.spigotmc.org/resources/viabackwards.27448/),
[ViaRewind](https://www.spigotmc.org/resources/viarewind.52109/) or
[ProtocolSupport](https://www.spigotmc.org/resources/protocolsupport.7201/).

When looking up the exact dependencies of uLib, you will notice that it uses [Paper](https://papermc.io/) and [Waterfall](https://github.com/PaperMC/Waterfall) instead of Spigot and BungeeCord.<br>
Paper/Waterfall provides better performance, and an expanded API which allows uLib to implement more and better features.<br>
uLib should work on Spigot as well, but some features could not work properly or not work at all as workarounds are needed to implement some features on Spigot.<br>
Consider using [Paper](https://papermc.io/), [Yatopia](https://yatopiamc.org/) or [Tuinity](https://github.com/Spottedleaf/Tuinity) and [Watefall](https://github.com/PaperMC/Waterfall) instead of Spigot and BungeeCord.

Another thing: Note the [license of this project](./LICENSE). Use this library at your own risk! The developer(s) / contributors of this project do not take any responsibility/liability in any way.

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
### Build Instructions
Linux (bash):
```bash
./gradlew build
```

Windows (cmd):
```cmd
./gradlew.bat build
```

### Attributions
 - NBTEditor (https://github.com/BananaPuncher714/NBTEditor) Copyright (c) 2018 BananaPuncher714, licensed under the [MIT license](https://raw.githubusercontent.com/BananaPuncher714/NBTEditor/master/LICENSE)
 - Bukkit (Copyright (c) 2020 Bukkit, licensed under the [GNU General Public License v3.0](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/raw/LICENCE.txt)):
    - YAML-Configuration Package (https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/configuration)
    - NumberConversions Class (https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/util/NumberConversions.java)
 - ParticleEffect Library (https://github.com/DarkBlade12/ParticleEffect) Copyright (c) 2016 DarkBlade12, custom license (see [here](https://gitlab.com/software4you.eu/ulib/-/blob/master/ulib-spigot-api/src/main/java/eu/software4you/minecraft/multiversion/BukkitReflectionUtils.java) and [here](https://gitlab.com/software4you.eu/ulib/-/blob/master/ulib-spigot-api/src/main/java/eu/software4you/minecraft/multiversion/ParticleEffect.java))