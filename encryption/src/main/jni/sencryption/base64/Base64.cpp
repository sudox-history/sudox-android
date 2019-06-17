#include <crypto++/base64.h>
#include <scipher/base64/Base64.h>
#include <jni.h>
#include <string>

using namespace std;
using namespace CryptoPP;

/**
 * Кодирует массив байтов с помощью Base64.
 *
 * @param decoded - незакодированные байты.
 * @param length - кол-во байтов для кодирования.
 */
string encodeToBase64(unsigned char *decoded, unsigned int length) {
    auto *encoder = new Base64Encoder(nullptr, false, 0);
    encoder->Put(decoded, static_cast<size_t>(length));
    encoder->MessageEnd();

    // Get result
    string encoded;
    word64 size = encoder->MaxRetrievable();
    encoded.resize(static_cast<unsigned int>(size));
    encoder->Get(reinterpret_cast<byte *>(&encoded[0]), encoded.size());

    // Clean memory & return result
    delete encoder;
    return encoded;
}

/**
 * Декодирует массив байтов с помощью Base64, возвращает в паре длину
 * декодированного сообщения и само сообщение.
 *
 * @param encoded - массив байтов в формате Base64.
 * @param length - кол-во байтов для декодирования.
 */
string decodeFromBase64(unsigned char *encoded, unsigned int length) {
    Base64Decoder decoder;
    decoder.Put(encoded, length);
    decoder.MessageEnd();

    // Get result
    string decoded;
    word64 size = decoder.MaxRetrievable();
    decoded.resize(static_cast<unsigned int>(size));
    decoder.Get(reinterpret_cast<byte *>(&decoded[0]), decoded.size());

    // Return size & result
    return decoded;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_decodeFromBase64(JNIEnv *env, jclass type,
                                                              jbyteArray encoded) {

    auto encodedLength = static_cast<unsigned int>(env->GetArrayLength(encoded));
    auto encodedNative = new unsigned char[encodedLength];

    // Convert to C++ type
    env->GetByteArrayRegion(encoded, 0, encodedLength, reinterpret_cast<jbyte *>(encodedNative));

    // Decode & map to jByteArray ...
    auto decoded = decodeFromBase64(encodedNative, encodedLength);
    auto length = decoded.size();
    jbyteArray jArray = env->NewByteArray(length);
    env->SetByteArrayRegion(jArray, 0, length, reinterpret_cast<const jbyte *>(decoded.c_str()));

    // Clean memory & return result ...
    delete[] encodedNative;
    return jArray;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sudox_encryption_Encryption_encodeToBase64(JNIEnv *env, jclass type,
                                                            jbyteArray decoded) {

    auto decodedLength = static_cast<unsigned int>(env->GetArrayLength(decoded));
    auto decodedNative = new unsigned char[decodedLength];

    // Convert to C++ type
    env->GetByteArrayRegion(decoded, 0, decodedLength, reinterpret_cast<jbyte *>(decodedNative));

    // Convert to Base64
    string encoded = encodeToBase64(decodedNative, decodedLength);

    // Clean memory
    delete[] decodedNative;

    // Convert to jstring & return as result
    return env->NewStringUTF(encoded.c_str());
}