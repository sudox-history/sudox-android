#include <openssl/rand.h>
#include <openssl/bn.h>
#include <jni.h>

extern "C"
JNIEXPORT jbyteArray JNIEXPORT
Java_com_sudox_encryption_Encryption_generateBytes(JNIEnv *env, __unused jclass type, jint count) {
    unsigned char buf[count];
    RAND_bytes(buf, count);

    jbyteArray res = env->NewByteArray(count);
    env->SetByteArrayRegion(res, 0, count, reinterpret_cast<jbyte *>(buf));
    return res;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sudox_encryption_Encryption_generateInt(__unused JNIEnv *env, __unused jclass type,
                                                 jint start, jint end) {
    auto intlen = sizeof(int);
    unsigned char output[intlen];
    RAND_bytes(output, intlen);

    int number = (int) output;
    number = (start + (number * (end - start) / INT_MAX));
    return number;
}