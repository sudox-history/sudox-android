cmake_minimum_required(VERSION 3.4)

#add_library(ssockets SHARED
#        ssockets/Client.cpp
#        ssockets/Helpers/SocketHelper.cpp
#        ssockets/Helpers/jni/JNIHelper.cpp
#        ssockets/Entries/JNICallback.cpp
#        ssockets/Worker.cpp)

set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS} -g -O0 -std=c++17 -fexceptions")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS} -O3 -std=c++17 -fexceptions -DNDEBUG")

add_library(ssockets SHARED
        SocketClient.cpp
        SocketListener.cpp
        JNIWrapper.cpp
        callbacks/JNICallback.cpp
        helpers/JNIHelper.cpp)

target_include_directories(ssockets PUBLIC .)