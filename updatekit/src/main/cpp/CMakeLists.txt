# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)
project("updatekit")

file(GLOB bzip bzip/*.c)

add_library(updatekit SHARED ${bzip} bsdiff.c bspatch.c updatekit.cpp)
include_directories(bzip)
find_library(log-lib log)

target_link_libraries(updatekit ${log-lib})