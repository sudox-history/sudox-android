#include <openssl/ossl_typ.h>
#include <openssl/pem.h>
#include <openssl/ec.h>
#include <jni.h>

constexpr char EC_SIGNATURE_KEY[] = "-----BEGIN PUBLIC KEY-----\n"
                                    "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEflkmgol1o7GFRjjB72BBbqhsRSI1SwHK\n"
                                    "/7357yJaEzwrBUt231AiPD2AG2MNaXr8SqDCUv3jbLzOB4+/bVkcimZVP2elvjsp\n"
                                    "/AdU1335LpuCufavSCrftkzD0MeiUBqc\n"
                                    "-----END PUBLIC KEY-----";

EVP_PKEY *load_key() {
    BIO *bio = BIO_new(BIO_s_mem());
    BIO_write(bio, EC_SIGNATURE_KEY, sizeof(EC_SIGNATURE_KEY));
    EC_KEY *pkey = PEM_read_bio_EC_PUBKEY(bio, nullptr, nullptr, nullptr);
    BIO_free(bio);

    EVP_PKEY *key = EVP_PKEY_new();
    EVP_PKEY_assign_EC_KEY(key, pkey);
    return key;
}

EVP_PKEY *EC_SIGNATURE_PKEY = load_key(); // NOLINT(cert-err58-cpp)

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sudox_encryption_Encryption_verifySignature(JNIEnv *env, __unused jclass type,
                                                     jbyteArray _message, jbyteArray _signature) {
    jsize _messagelen = env->GetArrayLength(_message);
    unsigned char message[_messagelen];
    env->GetByteArrayRegion(_message, 0, _messagelen, reinterpret_cast<jbyte *>(message));

    jsize _signaturelen = env->GetArrayLength(_signature);
    unsigned char signature[_signaturelen];
    env->GetByteArrayRegion(_signature, 0, _signaturelen, reinterpret_cast<jbyte *>(signature));

    EVP_MD_CTX *ctx = EVP_MD_CTX_new();
    const EVP_MD *md = EVP_sha224();
    EVP_DigestVerifyInit(ctx, nullptr, md, nullptr, EC_SIGNATURE_PKEY);
    EVP_DigestVerifyUpdate(ctx, message, sizeof(message));

    int res = EVP_DigestVerifyFinal(ctx, signature, sizeof(signature));
    EVP_MD_CTX_free(ctx);
    return static_cast<jboolean>(res == 1 ? JNI_TRUE : JNI_FALSE);
}
