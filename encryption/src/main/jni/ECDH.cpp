#include <openssl/ecdh.h>
#include <openssl/evp.h>
#include <openssl/bn.h>
#include <jni.h>

extern "C"
JNIEXPORT jobject JNICALL
Java_com_sudox_encryption_Encryption_startECDH(JNIEnv *env, __unused jclass type) {
    EVP_PKEY_CTX *params_ctx = EVP_PKEY_CTX_new_id(EVP_PKEY_EC, nullptr);
    EVP_PKEY *params = EVP_PKEY_new();
    EVP_PKEY_paramgen_init(params_ctx);
    EVP_PKEY_CTX_set_ec_paramgen_curve_nid(params_ctx, NID_secp384r1);
    EVP_PKEY_paramgen(params_ctx, &params);

    EVP_PKEY_CTX *keygen_ctx = EVP_PKEY_CTX_new(params, nullptr);
    EVP_PKEY *keypair = EVP_PKEY_new();
    EVP_PKEY_keygen_init(keygen_ctx);
    EVP_PKEY_keygen(keygen_ctx, &keypair);

    EC_KEY *eckey = EVP_PKEY_get1_EC_KEY(keypair);
    EC_GROUP *ecgroup = EC_GROUP_new_by_curve_name(NID_secp384r1);
    const EC_POINT *pub = EC_KEY_get0_public_key(eckey);

    size_t pubbuflen = 0;
    unsigned char *pubbuf;
    pubbuflen = EC_POINT_point2buf(ecgroup, pub, POINT_CONVERSION_UNCOMPRESSED, &pubbuf, nullptr);

    EVP_PKEY_CTX_free(params_ctx);
    EVP_PKEY_CTX_free(keygen_ctx);
    EVP_PKEY_free(params);
    EC_GROUP_free(ecgroup);
    EC_KEY_free(eckey);
    OPENSSL_free(pubbuf);

    // pubbuflen can't be greater than 2^32 in it's situation
    auto pubkeyarrlen = static_cast<jsize>(pubbuflen);
    jbyteArray pubkeyarr = env->NewByteArray(static_cast<jsize>(pubbuflen));
    env->SetByteArrayRegion(pubkeyarr, 0, pubkeyarrlen, reinterpret_cast<jbyte *>(pubbuf));

    jclass session_class = env->FindClass("com/sudox/encryption/ECDHSession");
    jmethodID session_constructor = env->GetMethodID(session_class, "<init>", "(J[B)V");
    jobject session_object = env->NewObject(session_class, session_constructor, reinterpret_cast<jlong>(keypair),
                                            pubkeyarr);
    return session_object;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_finishECDH(JNIEnv *env, __unused jclass type, jlong keyPairPointer,
                                                jbyteArray _publicKey) {
    auto *keypair = reinterpret_cast<EVP_PKEY *>(keyPairPointer);
    EC_KEY *eckey = EVP_PKEY_get1_EC_KEY(keypair);

    jsize pubkeylen = env->GetArrayLength(_publicKey);
    unsigned char pubkeybuf[pubkeylen];
    env->GetByteArrayRegion(_publicKey, 0, pubkeylen, reinterpret_cast<jbyte *>(pubkeybuf));

    auto *prvkeybn = const_cast<BIGNUM *>(EC_KEY_get0_private_key(eckey));
    EVP_PKEY *prvkey = EVP_PKEY_new();

    EC_KEY *prvckey_ec = EC_KEY_new_by_curve_name(NID_secp384r1);
    EC_KEY_set_private_key(prvckey_ec, prvkeybn);
    EVP_PKEY_set1_EC_KEY(prvkey, prvckey_ec);
    EVP_PKEY_free(keypair);
    EC_KEY_free(prvckey_ec);
    EC_KEY_free(eckey);
    BN_free(prvkeybn);

    EVP_PKEY *pubkey = EVP_PKEY_new();
    EC_GROUP *ecgroup = EC_GROUP_new_by_curve_name(NID_secp384r1);
    EC_KEY *pubkey_ec = EC_KEY_new_by_curve_name(NID_secp384r1);
    EC_POINT *pubkey_ecpoint = EC_POINT_new(ecgroup);
    BIGNUM *pubbn = BN_new();

    BN_bin2bn(pubkeybuf, pubkeylen, pubbn);
    EC_POINT_bn2point(ecgroup, pubbn, pubkey_ecpoint, nullptr);
    EC_KEY_set_public_key(pubkey_ec, pubkey_ecpoint);
    EVP_PKEY_set1_EC_KEY(pubkey, pubkey_ec);
    EC_POINT_free(pubkey_ecpoint);
    EC_GROUP_free(ecgroup);
    EC_KEY_free(pubkey_ec);
    BN_free(pubbn);

    EVP_PKEY_CTX *ctx = EVP_PKEY_CTX_new(prvkey, nullptr);
    EVP_PKEY_derive_init(ctx);
    EVP_PKEY_derive_set_peer(ctx, pubkey);
    size_t secretlen;
    int status = 0;

    EVP_PKEY_derive(ctx, nullptr, &secretlen);
    unsigned char secret[secretlen];
    status = EVP_PKEY_derive(ctx, secret, &secretlen);

    EVP_PKEY_free(prvkey);
    EVP_PKEY_free(pubkey);

    if (status <= 0) {
        return nullptr;
    }

    // pubbuflen can't be greater than 2^32 in it's situation
    auto secretsize = static_cast<jsize>(secretlen);
    jbyteArray res = env->NewByteArray(secretsize);
    env->SetByteArrayRegion(res, 0, secretsize, reinterpret_cast<jbyte *>(secret));
    return res;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_encryption_Encryption_closeECDH(__unused JNIEnv *env, __unused jclass type, jlong keyPairPointer) {
    auto *keypair = reinterpret_cast<EVP_PKEY *>(keyPairPointer);
    EVP_PKEY_free(keypair);
}