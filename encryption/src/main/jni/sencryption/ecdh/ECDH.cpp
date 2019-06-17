#include <scipher/ecdh/ECDH.h>
#include <scipher/random/Random.h>
#include <scipher/common/KeysPair.h>
#include <crypto++/asn.h>
#include <crypto++/oids.h>
#include <crypto++/ecp.h>
#include <crypto++/eccrypto.h>
#include <unordered_map>
#include <jni.h>
#include <utility>
#include <stdexcept>

using namespace std;
using namespace CryptoPP;

// Таблица для доступа к ключам через JNI.
static unordered_map<unsigned int, KeysPair> keysMap;

// Эллиптическая кривая и генератор ключей на её основе.
static OID curve = ASN1::secp384r1();
static ECDH<ECP>::Domain generator(curve);

/**
 * Производит расчет секретного ключа на основе приватного ключа и публичного ключа собеседника.
 *
 * @param privateKey - приватный ключ
 * @param recipientPublicKey - публичный ключ собеседника.
 */
SecByteBlock calculateSecretKey(unsigned char *privateKey,
                                          unsigned char *recipientPublicKey) {

    SecByteBlock secretKey(generator.AgreedValueLength());

    // Exception will be throw if agreement does not will be successfully
    if (!generator.Agree(secretKey, privateKey, recipientPublicKey)) {
        throw runtime_error("Failed to reach secret key");
    }

    return secretKey;
}

/**
 * Генерирует пару ключей (приватный и публичный) и выдает их ID в хранилище в качестве результата.
 */
unsigned int generateKeysPair() {
    SecByteBlock keyPrivate(generator.PrivateKeyLength()), keyPublic(
            generator.PublicKeyLength());
    generator.GenerateKeyPair(randomPool, keyPrivate, keyPublic);

    // Map to KeysPair structure
    auto pair = KeysPair();
    pair.keyPrivate = keyPrivate;
    pair.keyPublic = keyPublic;

    // Adding to list ...
    auto id = keysMap.size() + 1;
    keysMap.insert(std::pair<unsigned int, KeysPair>(id, pair));

    // Return id as result
    return id;
}

/**
 * Ищет пару ключей по её ID.
 *
 * @param pairId - ID пары, полученный в ходе добавления записи.
 */
KeysPair *findKeyPair(unsigned int pairId) {
    auto iterator = keysMap.find(pairId);

    // Returning pair when it founded.
    if (iterator != keysMap.end()) {
        return &iterator->second;
    }

    // Handle situation when pair not found
    return nullptr;
}

/**
 * Выдает приватный ключ из хранилища по ID его пары.
 *
 * @param pairId - ID пары, полученный в ходе добавления записи.
 */
SecByteBlock *getPrivateKey(unsigned int pairId) {
    auto pair = findKeyPair(pairId);

    // Returning private key when pair founded.
    if (pair != nullptr) {
        return &pair->keyPrivate;
    }

    // Handle situation when pair not found
    return nullptr;
}

/**
 * Выдает публичный ключ из хранилища по ID его пары.
 *
 * @param pairId - ID пары, полученный в ходе добавления записи.
 */
SecByteBlock *getPublicKey(unsigned int pairId) {
    auto pair = findKeyPair(pairId);

    // Returning public key when pair founded.
    if (pair != nullptr) {
        return &pair->keyPublic;
    }

    // Handle situation when pair not found
    return nullptr;
}

/**
 * Удаляет все пары ключей из хранилища.
 */
void removeAllKeysPairs() {
    // Not needed clean table when it's empty
    if (!keysMap.empty()) {
        keysMap.clear();
    }
}

/**
 * Удаляет пару ключей из хранилища.
 *
 * @param pairId - ID пары, полученный в ходе добавления записи.
 */
void removeKeysPair(unsigned int pairId) {
    // Will return 0 if pair with given id not will be founded
    keysMap.erase(pairId);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_calculateSecretKey(JNIEnv *env, jclass type,
                                                                jbyteArray privateKey,
                                                                jbyteArray recipientPublicKey) {

    auto privateKeyLength = static_cast<unsigned int>(env->GetArrayLength(privateKey));
    auto privateKeyNative = new unsigned char[privateKeyLength];
    auto recipientPublicKeyLength = static_cast<unsigned int>(env->GetArrayLength(
            recipientPublicKey));
    auto recipientPublicKeyNative = new unsigned char[recipientPublicKeyLength];

    // Convert to C++ type
    env->GetByteArrayRegion(privateKey, 0, privateKeyLength,
                            reinterpret_cast<jbyte *>(privateKeyNative));
    env->GetByteArrayRegion(recipientPublicKey, 0, recipientPublicKeyLength,
                            reinterpret_cast<jbyte *>(recipientPublicKeyNative));

    try {
        SecByteBlock secretKey = calculateSecretKey(privateKeyNative, recipientPublicKeyNative);

        // Map to jByteArray
        auto length = secretKey.size();
        jbyteArray jArray = env->NewByteArray(length);
        env->SetByteArrayRegion(jArray, 0, length, reinterpret_cast<const jbyte *>(secretKey.data()));

        // Cleaning the RAM & returning the result ...
        delete[] privateKeyNative;
        delete[] recipientPublicKeyNative;
        return jArray;
    } catch (runtime_error error) {
        delete[] privateKeyNative;
        delete[] recipientPublicKeyNative;
        return nullptr;
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sudox_encryption_Encryption_generateKeysPair(JNIEnv *env, jclass type) {
    return generateKeysPair();
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_getPrivateKey(JNIEnv *env, jclass type, jint pairId) {
    SecByteBlock *key = getPrivateKey(static_cast<unsigned int>(pairId));

    // If key not found empty array will be returned
    if (key == nullptr) {
        return nullptr;
    }

    // Mapping result to jByteArray
    auto size = key->size();
    jbyteArray jArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jArray, 0, size, reinterpret_cast<const jbyte *>(key->data()));

    // Returning result
    return jArray;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_getPublicKey(JNIEnv *env, jclass type, jint pairId) {
    SecByteBlock *key = getPublicKey(static_cast<unsigned int>(pairId));

    // If key not found empty array will be returned
    if (key == nullptr) {
        return nullptr;
    }

    // Mapping result to jByteArray
    auto size = key->size();
    jbyteArray jArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jArray, 0, size, reinterpret_cast<const jbyte *>(key->data()));

    // Returning result
    return jArray;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_encryption_Encryption_removeAllKeysPairs(JNIEnv *env, jclass type) {
    removeAllKeysPairs();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_encryption_Encryption_removeKeysPair(JNIEnv *env, jclass type, jint pairId) {
    removeKeysPair(static_cast<unsigned int>(pairId));
}