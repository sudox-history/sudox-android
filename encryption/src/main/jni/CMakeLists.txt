cmake_minimum_required(VERSION 3.4)
include(openssl/CMakeLists.txt)

add_library(sencryption SHARED
        Random.cpp
        HMAC.cpp
        ECDSA.cpp
        ECDH.cpp
        AES.cpp)

target_include_directories(sencryption PUBLIC .)
target_link_libraries(sencryption libcrypto)