#include <openssl/evp.h>
#include <jni.h>

constexpr int NEED_KEY_SIZE = 24;
constexpr int NEED_IV_SIZE = 16;

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_encryptWithAES(JNIEnv *env, __unused jclass type,
                                                    jbyteArray _key, jbyteArray _iv,
                                                    jbyteArray _message) {
    jsize _keylen = env->GetArrayLength(_key);
    jsize _ivlen = env->GetArrayLength(_iv);

    if (_keylen != NEED_KEY_SIZE || _ivlen != NEED_IV_SIZE) {
        return nullptr;
    }

    unsigned char key[_keylen];
    unsigned char iv[_ivlen];
    env->GetByteArrayRegion(_key, 0, _keylen, reinterpret_cast<jbyte *>(key));
    env->GetByteArrayRegion(_iv, 0, _ivlen, reinterpret_cast<jbyte *>(iv));

    jsize _messagelen = env->GetArrayLength(_message);
    unsigned char message[_messagelen];
    env->GetByteArrayRegion(_message, 0, _messagelen, reinterpret_cast<jbyte *>(message));

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    const EVP_CIPHER *cipher = EVP_aes_192_ctr();

    int len;
    int cipherlen;
    unsigned char out[_messagelen];

    EVP_EncryptInit_ex(ctx, cipher, nullptr, key, iv);
    EVP_EncryptUpdate(ctx, out, &len, message, _messagelen);
    cipherlen = len;

    EVP_EncryptFinal_ex(ctx, out + len, &len);
    cipherlen += len;
    EVP_CIPHER_CTX_free(ctx);

    jbyteArray res = env->NewByteArray(cipherlen);
    env->SetByteArrayRegion(res, 0, cipherlen, reinterpret_cast<jbyte *>(out));
    return res;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_decryptWithAES(JNIEnv *env, __unused jclass type,
                                                    jbyteArray _key, jbyteArray _iv,
                                                    jbyteArray _message) {
    jsize _keylen = env->GetArrayLength(_key);
    jsize _ivlen = env->GetArrayLength(_iv);

    if (_keylen != NEED_KEY_SIZE || _ivlen != NEED_IV_SIZE) {
        return nullptr;
    }

    unsigned char key[_keylen];
    unsigned char iv[_ivlen];
    env->GetByteArrayRegion(_key, 0, _keylen, reinterpret_cast<jbyte *>(key));
    env->GetByteArrayRegion(_iv, 0, _ivlen, reinterpret_cast<jbyte *>(iv));

    jsize _messagelen = env->GetArrayLength(_message);
    unsigned char message[_messagelen];
    env->GetByteArrayRegion(_message, 0, _messagelen, reinterpret_cast<jbyte *>(message));

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    const EVP_CIPHER *cipher = EVP_aes_192_ctr();

    int len;
    int plainlen;
    unsigned char out[_messagelen];

    EVP_DecryptInit_ex(ctx, cipher, nullptr, key, iv);
    EVP_DecryptUpdate(ctx, out, &len, message, _messagelen);
    plainlen = len;

    EVP_DecryptFinal_ex(ctx, out + len, &len);
    plainlen += len;
    EVP_CIPHER_CTX_free(ctx);

    jbyteArray res = env->NewByteArray(plainlen);
    env->SetByteArrayRegion(res, 0, plainlen, reinterpret_cast<jbyte *>(out));
    return res;
}
