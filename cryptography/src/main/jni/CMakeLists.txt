cmake_minimum_required(VERSION 3.10)

add_library(libsodium STATIC IMPORTED)
set_target_properties(libsodium PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libsodium.a)
set_property(TARGET libsodium APPEND PROPERTY INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_SOURCE_DIR}/include)

add_library(libcryptography SHARED ChaCha20.cpp BLAKE2b.cpp Random.cpp)
target_include_directories(libcryptography PUBLIC .)
target_link_libraries(libcryptography libsodium)