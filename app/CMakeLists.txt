cmake_minimum_required(VERSION 3.4.1)
add_executable(
    wildbox.so
    src/main/c/wildbox.c
)

target_link_options(
    wildbox.so
    PRIVATE "-static"
)

