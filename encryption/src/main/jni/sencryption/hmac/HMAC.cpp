#include <sencryption/hmac/HMAC.h>
#include <jni.h>
#include <crypto++/sha.h>
#include <crypto++/hmac.h>
#include <crypto++/filters.h>

using namespace std;
using namespace CryptoPP;

/**
 * Вычисляет HMAC сообщения с помощью SHA-224 на основе секретного ключа.
 *
 * @param key - секретный ключ
 * @param keyLength - длина секретного ключа
 * @param message - сообщение
 * @param messageLength - длина сообщения
 */
string calculateHMAC(unsigned char *key, unsigned int keyLength,
                          unsigned char *message,
                          unsigned int messageLength) {

    // Calculating the HMAC ...
    string mac;
    HMAC<SHA224> hmac(key, keyLength);
    auto sink = new StringSink(mac);
    auto filter = new HashFilter(hmac, sink);
    StringSource source(message, messageLength, true, filter);

    // Returning the result ...
    return mac;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_calculateHMAC(JNIEnv *env, jclass type,
                                                           jbyteArray key,
                                                           jbyteArray message) {

    auto keyLength = static_cast<unsigned int>(env->GetArrayLength(key));
    auto keyNative = new unsigned char[keyLength];
    auto messageLength = static_cast<unsigned int>(env->GetArrayLength(message));
    auto messageNative = new unsigned char[messageLength];

    // Convert to C++ type
    env->GetByteArrayRegion(key, 0, keyLength, reinterpret_cast<jbyte *>(keyNative));
    env->GetByteArrayRegion(message, 0, messageLength, reinterpret_cast<jbyte *>(messageNative));

    // Calculate HMAC
    string hmac = calculateHMAC(keyNative, keyLength, messageNative, messageLength);

    // Mapping result to jByteArray
    auto length = hmac.size();
    jbyteArray jArray = env->NewByteArray(length);
    env->SetByteArrayRegion(jArray, 0, length, reinterpret_cast<const jbyte *>(hmac.c_str()));

    // Cleaning RAM ...
    delete[] keyNative;
    delete[] messageNative;

    // Returning result
    return jArray;
}