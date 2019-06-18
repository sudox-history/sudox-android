#include <sencryption/aes/AES.h>
#include <crypto++/aes.h>
#include <crypto++/modes.h>
#include <crypto++/filters.h>
#include <jni.h>
#include <string>

using namespace std;
using namespace CryptoPP;

/**
 * Расшифровывает сообщение с помощью AES.
 *
 * @param key - ключ шифрования
 * @param keyLength - длина ключа шифрования
 * @param iv - инициализирующий вектор
 * @param ivLength - длина инициализирующего вектора
 * @param message - сообщение
 * @param messageLength - длина сообщения
 */
string decryptWithAES(unsigned char *key, unsigned int keyLength,
                      unsigned char *iv, unsigned int ivLength,
                      unsigned char *message, unsigned int messageLength) {

    // Decrypting ...
    CTR_Mode<AES>::Decryption decryption(key, keyLength, iv, ivLength);
    string decrypted;
    auto *sink = new StringSink(decrypted);
    auto *filter = new StreamTransformationFilter(decryption, sink);
    StringSource source(message, messageLength, true, filter);

    // Return result ...
    return decrypted;
}

/**
 * Зашифровывает сообщение с помощью AES.
 *
 * @param key - ключ шифрования
 * @param keyLength - длина ключа шифрования
 * @param iv - инициализирующий вектор
 * @param ivLength - длина инициализирующего вектора
 * @param message - сообщение
 * @param messageLength - длина сообщения
 */
string encryptWithAES(unsigned char *key, unsigned int keyLength,
                      unsigned char *iv, unsigned int ivLength,
                      unsigned char *message, unsigned int messageLength) {

    // Encrypting ...
    string encrypted;
    CTR_Mode<AES>::Encryption encryption(key, keyLength, iv, ivLength);
    auto *sink = new StringSink(encrypted);
    auto *filter = new StreamTransformationFilter(encryption, sink);
    StringSource source(message, messageLength, true, filter);

    // Return result ...
    return encrypted;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_decryptWithAES(JNIEnv *env, jclass type,
                                            jbyteArray key,
                                            jbyteArray iv, jbyteArray message) {

    auto keyLength = static_cast<unsigned int>(env->GetArrayLength(key));
    auto keyNative = new unsigned char[keyLength];
    auto ivLength = static_cast<unsigned int>(env->GetArrayLength(iv));
    auto ivNative = new unsigned char[ivLength];
    auto messageLength = static_cast<unsigned int>(env->GetArrayLength(message));
    auto messageNative = new unsigned char[messageLength];

    // Convert to C++ type
    env->GetByteArrayRegion(key, 0, keyLength, reinterpret_cast<jbyte *>(keyNative));
    env->GetByteArrayRegion(iv, 0, ivLength, reinterpret_cast<jbyte *>(ivNative));
    env->GetByteArrayRegion(message, 0, messageLength, reinterpret_cast<jbyte *>(messageNative));

    // Decrypting and mapping result to jByteArray
    try {
        string decrypted = decryptWithAES(keyNative, keyLength, ivNative, ivLength, messageNative,
                                          messageLength);
        auto size = decrypted.size();
        jbyteArray jArray = env->NewByteArray(size);
        env->SetByteArrayRegion(jArray, 0, size,
                                reinterpret_cast<const jbyte *>(decrypted.c_str()));

        // Cleaning the RAM ...
        delete[] keyNative;
        delete[] ivNative;
        delete[] messageNative;
        return jArray;
    } catch (CryptoPP::Exception &e) {
        delete[] keyNative;
        delete[] ivNative;
        delete[] messageNative;
        return nullptr;
    }
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_encryptWithAES(JNIEnv *env, jclass type,
                                            jbyteArray key,
                                            jbyteArray iv, jbyteArray message) {

    auto keyLength = static_cast<unsigned int>(env->GetArrayLength(key));
    auto keyNative = new unsigned char[keyLength];
    auto ivLength = static_cast<unsigned int>(env->GetArrayLength(iv));
    auto ivNative = new unsigned char[ivLength];
    auto messageLength = static_cast<unsigned int>(env->GetArrayLength(message));
    auto messageNative = new unsigned char[messageLength];

    // Convert to C++ type
    env->GetByteArrayRegion(key, 0, keyLength, reinterpret_cast<jbyte *>(keyNative));
    env->GetByteArrayRegion(iv, 0, ivLength, reinterpret_cast<jbyte *>(ivNative));
    env->GetByteArrayRegion(message, 0, messageLength, reinterpret_cast<jbyte *>(messageNative));

    // Encrypting & mapping result to jByteArray
    string encrypted = encryptWithAES(keyNative, keyLength, ivNative, ivLength, messageNative,
                                      messageLength);
    auto size = encrypted.size();
    jbyteArray jArray = env->NewByteArray(static_cast<jsize>(size));
    env->SetByteArrayRegion(jArray, 0, static_cast<jsize>(size),
                            reinterpret_cast<const jbyte *>(encrypted.c_str()));

    // Cleaning the RAM ...
    delete[] keyNative;
    delete[] ivNative;
    delete[] messageNative;

    // Returning the result ...
    return jArray;
}