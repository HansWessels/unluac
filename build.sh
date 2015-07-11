#! /bin/bash

pushd "$( dirname "$0" )"			2> /dev/null > /dev/null

#
# compile all java sources:
#
mkdir build 						2> /dev/null > /dev/null
#javac -verbose -deprecation -Werror -d build -sourcepath src  src/unluac/*.java
javac -deprecation -Werror -d build -sourcepath src  src/unluac/*.java

#
# bundle in a JAR file with a suitable Manifest file:
#
# (Note #1: Note the -C parameter: without that you're fucked!)
# (Note #2: also don't write the JAR file to the build directory or you'll have trouble re-building the next time -- or you have to spec your class files more precisely than this '.' at the end of the jar command below!)
#
mkdir bin 							2> /dev/null > /dev/null
#jar -cvfm bin/unluac.jar  src/unluac/Manifest.mf  -C build  .
jar -cfm bin/unluac.jar  src/unluac/Manifest.mf  -C build  .

#
# DEPLOY:
#
# and place the new compiled JAR file in the active tool directory:
#
if test -d ../../whereigo_bulk_decompilercrypter/tools/ ; then
	cp bin/unluac.jar ../../whereigo_bulk_decompilercrypter/tools/
fi

popd								2> /dev/null > /dev/null
