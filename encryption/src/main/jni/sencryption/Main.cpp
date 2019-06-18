#include <sencryption/random/Random.h>
#include <sencryption/ecdsa/ECDSA.h>
#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_encryption_Encryption_initLibrary(JNIEnv *env, jclass type) {
    initRandom();
    initECDSA();
}