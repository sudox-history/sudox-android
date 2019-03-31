#include <scipher/random/Random.h>
#include <scipher/ecdsa/ECDSA.h>
#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_protocol_helpers_CipherHelper_initLibrary(JNIEnv *env, jclass type) {
    initRandom();
    initECDSA();
}