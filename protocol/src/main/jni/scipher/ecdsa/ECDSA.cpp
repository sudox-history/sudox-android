#include <scipher/ecdsa/ECDSA.h>
#include <scipher/base64/Base64.h>
#include <scipher/random/Random.h>
#include <crypto++/ecp.h>
#include <crypto++/sha.h>
#include <crypto++/eccrypto.h>
#include <crypto++/files.h>
#include <crypto++/base64.h>
#include <crypto++/pem.h>
#include <jni.h>

static CryptoPP::ECDSA<CryptoPP::ECP, CryptoPP::SHA224>::PublicKey key;
static std::string PUBLIC_KEY_BODY = "-----BEGIN PUBLIC KEY-----\n"
                                     "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEflkmgol1o7GFRjjB72BBbqhsRSI1SwHK\n"
                                     "/7357yJaEzwrBUt231AiPD2AG2MNaXr8SqDCUv3jbLzOB4+/bVkcimZVP2elvjsp\n"
                                     "/AdU1335LpuCufavSCrftkzD0MeiUBqc\n"
                                     "-----END PUBLIC KEY-----";

/**
 * Проверяет данные на соответствие публичного ключа подписи.
 * Перед вызовом обязательно должен быть инициализирован верификатор.
 *
 * @param message - сообщение
 * @param messageLength - длина сообщения
 * @param signature - подпись сообщения
 * @param signatureLength - длина подписи сообщения
 */
bool verifyMessageWithECDSA(unsigned char *message, unsigned int messageLength,
                            unsigned char *signature, unsigned int signatureLength) {

    CryptoPP::ECDSA<CryptoPP::ECP, CryptoPP::SHA224>::Verifier verifier(key);

    // Check, that signature not ASN.1 & map to IEEE P1363 if needed
    if (signature[0] == 0x30 && signature[2] == 0x02) {
        unsigned int rLength = signature[3];
        unsigned int sLength = signature[4 + rLength + 1];
        unsigned int ieeeSignatureLength = rLength + sLength;
        auto *ieeeSignatureNative = new unsigned char[signatureLength];

        // Converting ...
        CryptoPP::DSAConvertSignatureFormat(ieeeSignatureNative, ieeeSignatureLength,
                                            CryptoPP::DSA_P1363,
                                            signature, signatureLength, CryptoPP::DSA_DER);

        // Write new signatures
        signatureLength = ieeeSignatureLength;
        signature = ieeeSignatureNative;
    }

    // Verifying ...
    bool result = verifier.VerifyMessage((const CryptoPP::byte *) &message[0], messageLength,
                                         (const CryptoPP::byte *) &signature[0], signatureLength);

    // Mapping to jBoolean
    return result;
}

/**
 * Читает публичный ключ, создает верификатора подписи с публичным ключем.
 */
void initECDSA() {
    CryptoPP::StringSource source(PUBLIC_KEY_BODY, true);
    CryptoPP::PEM_Load(source, key);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sudox_protocol_helpers_CipherHelper_verifyMessageWithECDSA(JNIEnv *env, jclass type,
                                                                    jbyteArray message,
                                                                    jbyteArray signature) {

    auto messageLength = static_cast<unsigned int>(env->GetArrayLength(message));
    auto messageNative = new unsigned char[messageLength];
    auto signatureLength = static_cast<unsigned int>(env->GetArrayLength(signature));
    auto signatureNative = new unsigned char[signatureLength];

    // Convert to C++ type
    env->GetByteArrayRegion(message, 0, messageLength, reinterpret_cast<jbyte *>(messageNative));
    env->GetByteArrayRegion(signature, 0, signatureLength,
                            reinterpret_cast<jbyte *>(signatureNative));

    // Checking signature ...
    bool result = verifyMessageWithECDSA(messageNative, messageLength, signatureNative,
                                         signatureLength);

    // Cleaning RAM ...
    delete[] messageNative;
    delete[] signatureNative;

    // Map to jBoolean and return result
    return static_cast<jboolean>(result ? JNI_TRUE : JNI_FALSE);
}