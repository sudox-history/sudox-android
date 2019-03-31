#include <crypto++/secblock.h>
#include <crypto++/osrng.h>
#include <crypto++/randpool.h>
#include <crypto++/filters.h>
#include <scipher/random/Random.h>
#include <scipher/base64/Base64.h>
#include <jni.h>

/**
 * Генерирует энтропию, создает пул генератора случайных чисел.
 */
void initRandom() {
    // Generate entropy
    CryptoPP::SecByteBlock seed(ENTROPY_SIZE);
    OS_GenerateRandomBlock(false, seed, seed.size());

    // Seeding random pool
    randomPool.IncorporateEntropy(seed, seed.size());
}

/**
 * Генерирует блок случайных байтов.
 * Перед вызовом обязательно должен быть инициализирован пул.
 *
 * @param length - длина массива.
 */
CryptoPP::SecByteBlock generateBytes(size_t length) {
    CryptoPP::SecByteBlock bytesBlock(length);
    randomPool.GenerateBlock(bytesBlock, bytesBlock.size());

    // Get bytes from block
    return bytesBlock;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_protocol_helpers_CipherHelper_generateBytes(JNIEnv *env, jclass type, jint length) {
    CryptoPP::SecByteBlock block = generateBytes(static_cast<size_t>(length));

    // Convert to jByteArray
    jbyteArray jArray = env->NewByteArray(length);
    env->SetByteArrayRegion(jArray, 0, length, (jbyte *) block.data());

    // Return result
    return jArray;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sudox_protocol_helpers_CipherHelper_generateBase64(JNIEnv *env, jclass type, jint length) {
    CryptoPP::SecByteBlock block = generateBytes(static_cast<size_t>(length));
    std::string encoded = encodeToBase64(block.data(), static_cast<unsigned int>(length));

    // Map to jString and return result
    return env->NewStringUTF(encoded.c_str());
}