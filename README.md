# unluac - LUA 5.x decompiler

`unluac` is a decompiler for Lua 5.1. It runs on Lua chunks that have been compiled with the standard Lua compiler. It requires that debugging information has not been stripped from the chunk. (By default, the Lua compiler includes this debugging information.)

This program is written in Java. A JAR package is available in the downloads section so you don't have to compile it. It runs from the command line and accepts a single argument: the file name of a Lua chunk. The decompiled code is printed to the standard output.

Here is an example usage of unluac:

```
java -jar unluac.jar myfile.lua > myfile_decompiled.lua
```

## How to compile `unluac` from source

```
# when sources are in src/unluac/...
cd src
mkdir build
javac -verbose -deprecation -Werror -d build unluac/*.java
```

