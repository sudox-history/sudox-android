#include <scipher/sha/SHA.h>
#include <crypto++/sha.h>
#include <jni.h>

/**
 * Вычисляет SHA-224 хэш переданного массива байтов.
 *
 * @param data - массив байтов
 * @param length - длина массива байтов
 */
std::string calculateSHA224(unsigned char *data, unsigned int length) {
    std::string digest;

    // Calculating hash ...
    CryptoPP::SHA224 hash;
    hash.Update(data, length);

    // Getting the result
    digest.resize(hash.DigestSize());
    hash.Final(reinterpret_cast<CryptoPP::byte *>(&digest[0]));

    // Return size
    return digest;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_protocol_helpers_CipherHelper_calculateSHA224(JNIEnv *env, jclass type, jbyteArray data) {
    auto dataLength = static_cast<unsigned int>(env->GetArrayLength(data));
    auto dataNative = new unsigned char[dataLength];

    // Convert to C++ type
    env->GetByteArrayRegion(data, 0, dataLength, reinterpret_cast<jbyte *>(dataNative));

    // Calculate hash & map to jByteArray
    std::string hash = calculateSHA224(dataNative, dataLength);
    size_t size = hash.size();
    jbyteArray jArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jArray, 0, size, reinterpret_cast<const jbyte *>(hash.c_str()));

    // Clean memory & return result ...
    delete[] dataNative;
    return jArray;
}


