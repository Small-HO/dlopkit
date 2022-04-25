# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# cmake 最低版本
cmake_minimum_required(VERSION 3.18.1)
# 项目
project(videokit)
# 1. 添加头文件目录
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)
# 2. 定义so库和头文件所在目录，方面后面使用
set(ffmpeg_lib_dir ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI})

# 3. 添加ffmpeg相关的so库
# avcodec
add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION ${ffmpeg_lib_dir}/libavcodec.so)
# avfilter
add_library(avfilter SHARED IMPORTED)
set_target_properties(avfilter PROPERTIES IMPORTED_LOCATION ${ffmpeg_lib_dir}/libavfilter.so)
# avformat
add_library(avformat SHARED IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION ${ffmpeg_lib_dir}/libavformat.so)
# avutil
add_library(avutil SHARED IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION ${ffmpeg_lib_dir}/libavutil.so)
# swresample
add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION ${ffmpeg_lib_dir}/libswresample.so)
# swscale
add_library(swscale SHARED IMPORTED)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION ${ffmpeg_lib_dir}/libswscale.so)
# 查找代码中使用到的系统库
find_library(log-lib log)
find_library(android-lib android)
# 配置目标so库编译信息
add_library(native-lib SHARED native-lib.cpp)
# 指定编译目标库时，cmake要链接的库
# libavcodec，libavformat用于对媒体文件进行处理、libavutil是一个通用的小型函数库，该库中实现了CRC校验码的产生，128位整数数学，最大公约数，整数开方，整数取对数，内存分配，大端小端格式的转换等功能
target_link_libraries(native-lib avcodec avfilter avformat avutil swresample swscale ${android-lib} ${log-lib})