add_library(scipher SHARED
        scipher/random/Random.cpp
        scipher/base64/Base64.cpp
        scipher/sha/SHA.cpp
        scipher/ecdsa/ECDSA.cpp
        scipher/hmac/HMAC.cpp
        scipher/ecdh/ECDH.cpp
        scipher/common/KeysPair.cpp
        scipher/aes/AES.cpp
        scipher/Main.cpp)

target_include_directories(scipher PUBLIC .)
target_link_libraries(scipher crypto++)