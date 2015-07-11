To run automated tests copy luac for LUA versions 5.0, 5.1 and 5.2 to 
the luac subdirectory.

You can download the binaries from 
https://sourceforge.net/projects/luabinaries

For Windows(tm) you will need these six files:
  lua50.dll
  luac50.exe
  lua5.1.dll
  luac5.1.exe
  luac52.dll
  luac52.exe

Currently the paths and filenames are hardcoded into the build script.

For building download and install Gradle from http://www.gradle.org
Build with "gradle --continue build".
