#!/bin/sh

# Use org.json from the Android Open Source Project
SDK_DIR=$(sed -n 's/sdk.dir=//p' local.properties)
SDK_VER=$(sed -n 's/.*targetSdkVersion //p' app/build.gradle)
ANDROID_JAR=$SDK_DIR/platforms/android-$SDK_VER/android.jar

(cd app/src/main/java && java -classpath $ANDROID_JAR:. uk/co/rogerstech/servalot/Servalot)

