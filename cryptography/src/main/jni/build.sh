cd libsodium

export NDK_PLATFORM="android-23"
export CFLAGS='-O3'

sh ./autogen.sh
sh ./dist-build/android-x86.sh
sh ./dist-build/android-x86_64.sh
sh ./dist-build/android-armv7-a.sh
sh ./dist-build/android-armv8-a.sh

rm -R ../include/*
rm -R ../libs/*/*

# Headers
mv libsodium-android-westmere/include/* ../include/

# Libs
mv libsodium-android-i686/lib/libsodium.a ../libs/x86/libsodium.a
mv libsodium-android-westmere/lib/libsodium.a ../libs/x86_64/libsodium.a
mv libsodium-android-armv7-a/lib/libsodium.a ../libs/armeabi-v7a/libsodium.a
mv libsodium-android-armv8-a/lib/libsodium.a ../libs/arm64-v8a/libsodium.a

cd ..
