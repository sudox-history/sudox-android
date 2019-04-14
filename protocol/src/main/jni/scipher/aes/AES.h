#ifndef SUDOX_ANDROID_AES_H
#define SUDOX_ANDROID_AES_H

#include <stdio.h>
#include <string>

using namespace std;

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
string decryptWithAES(unsigned char*, unsigned int, unsigned char*,
                      unsigned int, unsigned char*, unsigned int);

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
std::string encryptWithAES(unsigned char*, unsigned int, unsigned char*,
                           unsigned int, unsigned char*, unsigned int);

#endif //SUDOX_ANDROID_AES_H
