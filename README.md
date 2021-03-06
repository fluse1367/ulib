# uLib

This library is designed to ease process of developing standalone applications, Velocity, BungeeCord and Spigot plugins.

Copyright (c) 2021 [fluse1367](https://gitlab.com/fluse1367) / [software4you.eu](https://gitlab.com/software4you.eu)   
See "Included Software" (at the bottom) for copyright and license notice of included software.

Please also refer to the [documentation](docs/Readme.md).

|                                                                                                                                                                                                                                                        RELEASE BUILD |                                                                                                                                                                                                                                                       SNAPSHOT BUILD |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|                   ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-loader&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-loader%2Fmaven-metadata.xml) |                   ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-loader&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-loader%2Fmaven-metadata.xml) |
|               ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-core-api%2Fmaven-metadata.xml) |               ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&label=ulib-core-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-core-api%2Fmaven-metadata.xml) |
|         ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=orange&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml) |         ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=orange&label=ulib-spigot-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-spigot-api%2Fmaven-metadata.xml) |
| ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=yellow&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml) | ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=yellow&label=ulib-bungeecord-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-bungeecord-api%2Fmaven-metadata.xml) |
|       ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=aqua&label=ulib-velocity-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F19415500%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-velocity-api%2Fmaven-metadata.xml) |       ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=aqua&label=ulib-velocity-api&metadataUrl=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F26647460%2Fpackages%2Fmaven%2Feu%2Fsoftware4you%2Fulib%2Fulib-velocity-api%2Fmaven-metadata.xml) |

## Things to Know

- This library depends on recent spigot/bungeecord/velocity versions. That means it may not work as expected or may not
  work at all
  on older server versions. You will not receive any support, when using another server version than the one this
  library is built for. <br><br>
  If you want to use older server versions, consider a cross-version compatibility tool, like
  [ViaBackwards](https://www.spigotmc.org/resources/viabackwards.27448),
  [ViaRewind](https://www.spigotmc.org/resources/viarewind.52109) or
  [ProtocolSupport](https://www.spigotmc.org/resources/protocolsupport.7201).


- Minimum Java version is 17.


- When launching uLib for the first time (or if the respective caching folder was removed), it will download a few of
  dependencies/libraries.

## Disclaimer

Note the copyright and [license of this project](./LICENSE). Use this library at your own risk! The contributors of this
project do not take any responsibility/liability in any way.

# Included Software

The following 3rd-party software is included within this project:

- NBTEditor
  ([click](https://github.com/BananaPuncher714/NBTEditor/blob/ff303d06e16eed119079bae92b8ef5700ec35d87/src/main/java/io/github/bananapuncher714/nbteditor/NBTEditor.java))
  Copyright (c) 2018 [BananaPuncher714](https://github.com/BananaPuncher714), licensed under
  the [MIT license](https://github.com/BananaPuncher714/NBTEditor/blob/ff303d06e16eed119079bae92b8ef5700ec35d87/LICENSE)
- Apache Commons Lang v3 (Copyright (c) 2022 [The Apache Software Foundation](https://www.apache.org/), licensed under
  the [Apache License 2.0](https://github.com/apache/commons-lang/blob/9f1ac974e1b52c58b1950acdaaf3e0d5881409df/LICENSE.txt)):
  - SystemUtils
    Class ([click](https://github.com/apache/commons-lang/blob/9f1ac974e1b52c58b1950acdaaf3e0d5881409df/src/main/java/org/apache/commons/lang3/SystemUtils.java))
  - JavaVersion
    Class ([click](https://github.com/apache/commons-lang/blob/9f1ac974e1b52c58b1950acdaaf3e0d5881409df/src/main/java/org/apache/commons/lang3/JavaVersion.java))

---