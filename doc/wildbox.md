WIth LD, run busyBOX - 'wildbox'
================================

NOTE: This document describes a feature not yet implemented in Servalot. The low-level concepts that support this feature have been tried and work but the prototype is not user friendly. This document forms part of a user-first design.

Servalot optionally includes binaries from the Alpine Linux distro. To comply with W^X restrictions introduced Android 10, the binaries are included in the Servalot APK. Servalot can also share its included binaries with other installed apps so that they too can execute them. Servalot includes a utility called *wildbox*.

Inspired by busybox, wildbox determines what to execute based on the link name. As the path of the loader changes during each installation of the Servalot app, it cannot be hard coded into busybox. Once the app is installed and the path known, the busybox binary can no longer be modified as it is in a read-only directory as determined by the Android system. Therefore, to execute busybox the loader has to be explicitly specified when executing busybox. This is the responsibility of wildbox.

An alternative solution to wildbox would be to static link busybox so that it would not be dependant on a separate loader. However, this solution is not useful for other binaries that may be included in extension packages as they would all have to be statically linked.

Going wild
----------

In addition to busybox applets, wildbox supports execution of LLVM bitcode files and shell scripts by searching the PATH environment variable. For even greater flexibility wildbox has two parts; an executable and a helper script. The executable part resides in the Servalot's lib directory and is read-only. The executable part is responsible for executing a user defined helper script using the busybox sh applet. The helper script is a shell script that can reside in a writeable directory and can be user modified to give freedom to what can be executed. The helper script is located by wildbox by using the WILDBOX_HELPER environment variable. The helper script is executed using the busybox sh applet and it not limited to what Android's /system/bin/sh supports.

Including Executable Support
----------------------------

Before building and installing the APK, to include executable support, run the following on the build host PC

```
$ wildbox/get_alpine_pkgs.sh
```

This will download a collection of Alpine Linux packages and expand them. The ELF executables and shared objects are moved to the JNI directory to be included in the Servalot APK. Other files are archived up into a file called *wildbox.tgz* which is to be copied onto the device. Suggest /sdcard/Servalot/Download/wildbox.tgz which can then be expanded into the Servalot files directory using the command

```
"$SERVALOT_LIBS/ld-musl-aarch64.so" "$SERVALOT_LIBS/busybox.so" tar -zxvf /sdcard/Servalot/Download/wildbox.tgz 2>&1
```

Using Servalot Binaries from Terminal Apps
------------------------------------------

It is possible to execute Servalot binaries from Terminal Apps. This will be demonstrated using [ConnectBot](https://connectbot.org/). The wildbox helper script and environment setup can be written to a file on the (emulated) sdcard using the **export wildbox** button in the Servalot GUI.

From a local session in ConnectBot dot source the wildbox setup file

```
$ . /sdcard/Servalot/wildbox-setup.sh
```

