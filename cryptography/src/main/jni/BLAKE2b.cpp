#include <jni.h>
#include <sodium.h>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_ru_sudox_cryptography_BLAKE2b_hash(JNIEnv *env, __unused jclass clazz, jbyteArray _data) {
    jsize datalen = env->GetArrayLength(_data);
    unsigned char data[datalen];
    env->GetByteArrayRegion(_data, 0, datalen, reinterpret_cast<jbyte *>(data));

    unsigned char hash[crypto_generichash_BYTES];
    crypto_generichash(hash, sizeof(hash), data, sizeof(data), nullptr, 0);

    jbyteArray res = env->NewByteArray(sizeof(hash));
    env->SetByteArrayRegion(res, 0, sizeof(hash), reinterpret_cast<const jbyte *>(hash));

    return res;
}