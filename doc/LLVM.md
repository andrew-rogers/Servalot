Using LLVM in Servalot
======================

Servalot optionally includes the LLVM binaries from the Alpine Linux distro to support the compiling of C programs. To comply with W^X restrictions introduced Android 10, the binaries are included in the Servalot APK. Servalot can also share its LLVM support with other installed apps so that they too can execute C programs compiled in Servalot or even compile their own C programs using the Servalot installation.

Including LLVM Support
----------------------

Before building and installing the APK, to include LLVM support, run the following on the build host PC

```
$ wildbox/get-alpine-pkgs.sh llvm
```

This will download a collection of Alpine Linux packages and expand them. The ELF executables and shared objects are moved to the JNI directory to be included in the Servalot APK. Other files are archived up into a file called *wildbox.tgz* which is to be copied to /sdcard/Servalot/Download/wildbox.tgz on the device. This is then expanded into the Servalot files directory using the command

```
sh utils/wildbox-setup.sh 2>&1
```

This will also create the links to the executables and shared objects within the Servalot app.

Compile and execute a simple program
------------------------------------

```
cd utils/c-demo && make 2>&1
```

Executing User Compiled C Programs
----------------------------------

LLVM is used to compile C source code to LLVM bitcode files. These can then be executed using the LLWM JIT system, **lli**. These bitcode files can even be stored on the sdcard directory and shared with other apps as they do not require a file system with exec permissions. Any app using these bitcode files will either need their own **lli** or use the one from the Servalot app.
