add_library(sencryption SHARED
        sencryption/random/Random.cpp
        sencryption/base64/Base64.cpp
        sencryption/sha/SHA.cpp
        sencryption/ecdsa/ECDSA.cpp
        sencryption/hmac/HMAC.cpp
        sencryption/ecdh/ECDH.cpp
        sencryption/common/KeysPair.cpp
        sencryption/aes/AES.cpp
        sencryption/Main.cpp)

target_include_directories(sencryption PUBLIC .)
target_link_libraries(sencryption crypto++)