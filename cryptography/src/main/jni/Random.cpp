#include <sodium.h>
#include <jni.h>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_ru_sudox_cryptography_Random_generate(JNIEnv *env, __unused jclass clazz, jint length) {
    unsigned char bytes[length];
    randombytes_buf(bytes, sizeof(bytes));

    jbyteArray res = env->NewByteArray(sizeof(bytes));
    env->SetByteArrayRegion(res, 0, sizeof(bytes), reinterpret_cast<const jbyte *>(bytes));

    return res;
}