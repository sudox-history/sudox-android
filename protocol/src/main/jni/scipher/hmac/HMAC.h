#ifndef SUDOX_ANDROID_HMAC_H
#define SUDOX_ANDROID_HMAC_H

#include <stdio.h>
#include <string>

/**
 * Вычисляет HMAC сообщения с помощью SHA-224 на основе секретного ключа.
 *
 * @param key - секретный ключ
 * @param keyLength - длина секретного ключа
 * @param message - сообщение
 * @param messageLength - длина сообщения
 */
std::string calculateHMAC(unsigned char *, unsigned int, unsigned char *, unsigned int);

#endif //SUDOX_ANDROID_HMAC_H
