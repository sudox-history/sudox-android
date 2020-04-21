#include <jni.h>
#include <sodium.h>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_ru_sudox_cryptography_XChaCha20Poly1305_encryptData(JNIEnv *env, __unused jclass clazz, jbyteArray _key,
                                                         jbyteArray _nonce,
                                                         jbyteArray _data) {
    jsize keylen = env->GetArrayLength(_key);
    unsigned char key[keylen];
    env->GetByteArrayRegion(_key, 0, keylen, reinterpret_cast<jbyte *>(key));

    jsize noncelen = env->GetArrayLength(_nonce);
    unsigned char nonce[noncelen];
    env->GetByteArrayRegion(_nonce, 0, noncelen, reinterpret_cast<jbyte *>(nonce));

    jsize datalen = env->GetArrayLength(_data);
    unsigned char data[datalen];
    env->GetByteArrayRegion(_data, 0, datalen, reinterpret_cast<jbyte *>(data));

    unsigned char ciphertext[datalen + crypto_aead_xchacha20poly1305_ietf_ABYTES];
    unsigned long long ciphertextlen;
    crypto_aead_xchacha20poly1305_ietf_encrypt(ciphertext, &ciphertextlen, data, sizeof(data), nullptr, 0, nullptr, nonce, key);

    // P.S.: ciphertextlen никогда не будет отрицательным или больше чем INT_MAX
    jbyteArray res = env->NewByteArray(static_cast<jsize>(ciphertextlen));
    env->SetByteArrayRegion(res, 0, static_cast<jsize>(ciphertextlen), reinterpret_cast<const jbyte *>(ciphertext));

    return res;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_ru_sudox_cryptography_XChaCha20Poly1305_decryptData(JNIEnv *env, __unused jclass clazz,
                                                         jbyteArray _key,
                                                         jbyteArray _nonce,
                                                         jbyteArray _ciphertext) {

    jsize keylen = env->GetArrayLength(_key);
    unsigned char key[keylen];
    env->GetByteArrayRegion(_key, 0, keylen, reinterpret_cast<jbyte *>(key));

    jsize noncelen = env->GetArrayLength(_nonce);
    unsigned char nonce[noncelen];
    env->GetByteArrayRegion(_nonce, 0, noncelen, reinterpret_cast<jbyte *>(nonce));

    jsize ciphertextlen = env->GetArrayLength(_ciphertext);
    unsigned char ciphertext[ciphertextlen];
    env->GetByteArrayRegion(_ciphertext, 0, ciphertextlen, reinterpret_cast<jbyte *>(ciphertext));

    unsigned char decrypted[ciphertextlen];
    unsigned long long decryptedlen;

    if (crypto_aead_xchacha20poly1305_ietf_decrypt(decrypted, &decryptedlen, nullptr, ciphertext, sizeof(ciphertext), nullptr, 0,
                                                   nonce, key) != 0) {

        // TODO: Message losts!
        jclass exception_cls = env->FindClass("java/lang/SecurityException");
        env->ThrowNew(exception_cls, "Error during decryption");

        return nullptr;
    }

    // P.S.: decryptedlen никогда не будет отрицательным или больше чем INT_MAX
    jbyteArray res = env->NewByteArray(static_cast<jsize>(decryptedlen));
    env->SetByteArrayRegion(res, 0, static_cast<jsize>(decryptedlen), reinterpret_cast<const jbyte *>(decrypted));

    return res;
}