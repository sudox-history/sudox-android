#include <jni.h>
#include <sodium.h>

extern "C"
JNIEXPORT jobject JNICALL
Java_ru_sudox_cryptography_X25519_generateKeyPair(JNIEnv *env, __unused jclass clazz) {
    unsigned char pk[crypto_kx_PUBLICKEYBYTES], sk[crypto_kx_SECRETKEYBYTES];
    crypto_kx_keypair(pk, sk);

    jbyteArray jpk = env->NewByteArray(sizeof(pk));
    env->SetByteArrayRegion(jpk, 0, sizeof(pk), reinterpret_cast<const jbyte *>(pk));

    jbyteArray jsk = env->NewByteArray(sizeof(sk));
    env->SetByteArrayRegion(jsk, 0, sizeof(sk), reinterpret_cast<const jbyte *>(sk));

    jclass keypair_cls = env->FindClass("ru/sudox/cryptography/entries/KeyPair");
    jmethodID constructor_id = env->GetMethodID(keypair_cls, "<init>", "([B[B)V");
    return env->NewObject(keypair_cls, constructor_id, jpk, jsk);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_ru_sudox_cryptography_X25519_exchange(JNIEnv *env, __unused jclass clazz, jbyteArray _public_key, jbyteArray _secret_key,
                                           jbyteArray _recipient_public_key, jboolean is_server) {

    jsize rpkeylen = env->GetArrayLength(_recipient_public_key);
    jsize pkeylen = env->GetArrayLength(_public_key);
    jsize skeylen = env->GetArrayLength(_secret_key);

    unsigned char rpkey[rpkeylen];
    unsigned char pkey[pkeylen];
    unsigned char skey[skeylen];

    env->GetByteArrayRegion(_recipient_public_key, 0, rpkeylen, reinterpret_cast<jbyte *>(rpkey));
    env->GetByteArrayRegion(_public_key, 0, pkeylen, reinterpret_cast<jbyte *>(pkey));
    env->GetByteArrayRegion(_secret_key, 0, skeylen, reinterpret_cast<jbyte *>(skey));

    unsigned char rx[crypto_kx_SESSIONKEYBYTES], tx[crypto_kx_SESSIONKEYBYTES];
    int result = 0;

    if (is_server == JNI_TRUE) {
        result = crypto_kx_server_session_keys(rx, tx, pkey, skey, rpkey);
    } else {
        result = crypto_kx_client_session_keys(rx, tx, pkey, skey, rpkey);
    }

    if (result != 0) {
        jclass exception_cls = env->FindClass("java/lang/Exception");
        env->ThrowNew(exception_cls, "Error during keys exchanging");
        return nullptr;
    }

    jbyteArray rxkey = env->NewByteArray(sizeof(rx));
    jbyteArray txkey = env->NewByteArray(sizeof(tx));
    env->SetByteArrayRegion(rxkey, 0, sizeof(rx), reinterpret_cast<const jbyte *>(rx));
    env->SetByteArrayRegion(txkey, 0, sizeof(tx), reinterpret_cast<const jbyte *>(tx));

    jclass secretkeypair_cls = env->FindClass("ru/sudox/cryptography/entries/SecretKeyPair");
    jmethodID constructor_id = env->GetMethodID(secretkeypair_cls, "<init>", "([B[B)V");
    return env->NewObject(secretkeypair_cls, constructor_id, rxkey, txkey);
}