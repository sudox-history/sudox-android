#!/bin/bash

export ANDROID_NDK_HOME=/home/themax/Android/Sdk/ndk-bundle/

OPENSSL_DIR=openssl-1.1.1c
OUTPUT_INCLUDE=../output/include
OUTPUT_LIB=../output/libs

toolchains_path=$(python toolchains_path.py --ndk ${ANDROID_NDK_HOME})

CC=clang
PATH=$toolchains_path/bin:$PATH

cd ${OPENSSL_DIR}

for arch in android-arm android-arm64 android-x86 android-x86_64
do
    make clean
    ./Configure ${arch} no-autoalginit no-autoerrinit no-autoload-config no-capieng no-cms no-comp \
    no-ct no-deprecated no-dgram no-engine no-err no-gost no-nextprotoneg no-ocsp \
    no-psk no-shared no-sock no-srp no-srtp no-threads no-ts no-ssl no-tls no-dtls \
    no-aria no-bf no-blake2 no-cast no-chacha no-cmac no-des no-idea no-md4 no-mdc2 no-ocb no-poly1305 \
    no-rc2 no-rc4 no-rmd160 no-scrypt no-seed no-siphash no-sm2 no-sm3 no-sm4 no-whirlpool \
    -D__ANDROID_API__=21
    
    make
    
    if [ $arch == 'android-arm' ]
    then
        mkdir -p $OUTPUT_LIB/armeabi-v7a
        cp libcrypto.a $OUTPUT_LIB/armeabi-v7a/libcrypto.a
        cp libssl.a $OUTPUT_LIB/armeabi-v7a/libssl.a
    elif [ $arch == 'android-arm64' ]
    then
        mkdir -p $OUTPUT_LIB/arm64-v8a
        cp libcrypto.a $OUTPUT_LIB/arm64-v8a/libcrypto.a
        cp libssl.a $OUTPUT_LIB/arm64-v8a/libssl.a
    elif [ $arch == 'android-x86' ]
    then
        mkdir -p $OUTPUT_LIB/x86
        cp libcrypto.a $OUTPUT_LIB/x86/libcrypto.a
        cp libssl.a $OUTPUT_LIB/x86/libssl.a    
    elif [ $arch == 'android-x86_64' ]
    then
        mkdir -p $OUTPUT_LIB/x86_64
        cp libcrypto.a $OUTPUT_LIB/x86_64/libcrypto.a
        cp libssl.a $OUTPUT_LIB/x86_64/libssl.a
    fi
done

cp -RL include/openssl $OUTPUT_INCLUDE
