Using LLVM in Servalot
======================

Servalot optionally includes the LLVM binaries from the Alpine Linux distro to support the compiling of C programs. To comply with W^X restrictions introduced Android 10, the binaries are included in the Servalot APK. Servalot can also share its LLVM support with other installed apps so that they too can execute C programs compiled in Servalot or even compile their own C programs using the Servalot installation.

Including LLVM Support
----------------------

Before building and installing the APK, to include LLVM support, run the following on the build host PC

```
$ wildbox/get-alpine-pkgs.sh llvm
```

This will download a collection of Alpine Linux packages and expand them. The ELF executables and shared objects are moved to the JNI directory to be included in the Servalot APK. Other files are archived up into a file called *wildbox.tgz* which is to be copied onto the device. Suggest /sdcard/Servalot/Download/wildbox.tgz which can then be expanded into the Servalot files directory using the command

```
"$SERVALOT_LIBS/ld-musl-aarch64.so" "$SERVALOT_LIBS/busybox.so" tar -zxvf /sdcard/Servalot/Download/wildbox.tgz 2>&1
```

Links to the executables and shared objects are then created by running

```
"$SERVALOT_LIBS/ld-musl-aarch64.so" "$SERVALOT_LIBS/busybox.so" sh mklinks.sh 2>&1
```

Compile and execute a simple program
------------------------------------

```
busybox sh test_clang.sh 2>&1
```

Using Servalot Binaries from Terminal Apps
------------------------------------------

It is possible to execute Servalot binaries from Terminal Apps. This will be demonstrated using [ConnectBot](https://connectbot.org/). The name of the Servalot directory used to store the binaries is needed and this can be written to a file on the (emulated) sdcard using the following command in Servalot GUI

```
echo "export SERVALOT_LIBS=$SERVALOT_LIBS" > /sdcard/Servalot/env.sh 2>&1
```

Setup a local session in ConnectBot and cd to its files directory

```
$ cd /data/data/org.connectbot/files
```

Then dot source the Servalot env file created above

```
$ . /sdcard/Servalot/env.sh
```

Now the Alpine Linux busybox included in Servalot can be run from the ConnectBot session

```
$ $SERVALOT_LIBS/ld-musl-aarch64.so $SERVALOT_LIBS/busybox.so
```

Which should display the busybox version and supported functions.

Executing Shell scripts
-----------------------

Servalot includes a script called *exec_sh.so* that allows execution scripts in the *scripts* sub-directory. Multiple links can be provided in the apps writable directory pointing to *exec_sh.so*. The name of the script executed is determined from the link name. This link approach is inspired by busybox.

To do: Currently the *exec_sh.so* script sets a variable to /data/data/uk.co.rogerstech.servalot/files, this needs to be removed and an environment variable provided to allow use from other terminal apps.

Executing User Compiled C Programs
----------------------------------

LLVM is used to compile C source code to LLVM bitcode files. These can then be executed using the LLWM JIT system, **lli**. These bitcode files can even be stored on the sdcard directory and shared with other apps as they do not require a file system with exec permissions. Any app using these bitcode files will either need their own **lli** or use the one from the Servalot app as described above.
