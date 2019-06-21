#include <openssl/ossl_typ.h>
#include <openssl/evp.h>
#include <algorithm>
#include <jni.h>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_computeHMAC(JNIEnv *env, __unused jclass type, jbyteArray _key,
                                                   jbyteArray _message) {
    jsize _keylen = env->GetArrayLength(_key);
    unsigned char key[_keylen];
    env->GetByteArrayRegion(_key, 0, _keylen, reinterpret_cast<jbyte *>(key));

    jsize _messagelen = env->GetArrayLength(_message);
    unsigned char message[_messagelen];
    env->GetByteArrayRegion(_message, 0, _messagelen, reinterpret_cast<jbyte *>(message));

    EVP_MD_CTX *ctx = EVP_MD_CTX_create();
    EVP_PKEY *pkey = EVP_PKEY_new_mac_key(EVP_PKEY_HMAC, nullptr, key, _keylen);
    const EVP_MD *md = EVP_sha224();
    EVP_DigestInit_ex(ctx, md, nullptr);
    EVP_DigestSignInit(ctx, nullptr, md, nullptr, pkey);
    EVP_DigestSignUpdate(ctx, message, sizeof(message));

    size_t hmaclen;
    EVP_DigestSignFinal(ctx, nullptr, &hmaclen);
    unsigned char hmac[hmaclen];
    EVP_DigestSignFinal(ctx, hmac, &hmaclen);
    EVP_MD_CTX_destroy(ctx);

    jsize reslen = sizeof(hmac);
    jbyteArray res = env->NewByteArray(reslen);
    env->SetByteArrayRegion(res, 0, reslen, reinterpret_cast<jbyte *>(hmac));
    return res;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sudox_encryption_Encryption_verifyHMAC(JNIEnv *env, __unused jclass type, jbyteArray _key, jbyteArray _message,
                                                jbyteArray _hmac) {
    jsize _keylen = env->GetArrayLength(_key);
    unsigned char key[_keylen];
    env->GetByteArrayRegion(_key, 0, _keylen, reinterpret_cast<jbyte *>(key));

    jsize _messagelen = env->GetArrayLength(_message);
    unsigned char message[_messagelen];
    env->GetByteArrayRegion(_message, 0, _messagelen, reinterpret_cast<jbyte *>(message));

    jsize _hmaclen = env->GetArrayLength(_hmac);
    unsigned char hmac[_hmaclen];
    size_t hmaclen = sizeof(hmac);
    env->GetByteArrayRegion(_hmac, 0, _hmaclen, reinterpret_cast<jbyte *>(hmac));

    EVP_MD_CTX *ctx = EVP_MD_CTX_create();
    EVP_PKEY *pkey = EVP_PKEY_new_mac_key(EVP_PKEY_HMAC, nullptr, key, _keylen);
    const EVP_MD *md = EVP_sha224();
    EVP_DigestInit_ex(ctx, md, nullptr);
    EVP_DigestSignInit(ctx, nullptr, md, nullptr, pkey);
    EVP_DigestSignUpdate(ctx, message, sizeof(message));

    size_t original_hmaclen;
    EVP_DigestSignFinal(ctx, nullptr, &original_hmaclen);
    unsigned char original_hmac[original_hmaclen];
    EVP_DigestSignFinal(ctx, original_hmac, &original_hmaclen);
    EVP_MD_CTX_destroy(ctx);

    if (hmaclen != original_hmaclen) {
        return JNI_FALSE;
    }

    size_t size = hmaclen < original_hmaclen ? hmaclen : original_hmaclen;
    bool res = CRYPTO_memcmp(hmac, original_hmac, size) == 0;
    return static_cast<jboolean>(res == 1 ? JNI_TRUE : JNI_FALSE);
}