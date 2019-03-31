#ifndef SUDOX_ANDROID_BASE64_H
#define SUDOX_ANDROID_BASE64_H

#include <stdio.h>
#include <string>

/**
 * Кодирует массив байтов с помощью Base64.
 *
 * @param decoded - незакодированные байты.
 * @param length - кол-во байтов для кодирования.
 */
std::string encodeToBase64(unsigned char*, unsigned int);

/**
 * Декодирует массив байтов с помощью Base64, возвращает в паре длину
 * декодированного сообщения и само сообщение.
 *
 * @param encoded - массив байтов в формате Base64.
 * @param length - кол-во байтов для декодирования.
 */
std::string decodeFromBase64(unsigned char*, unsigned int);

#endif //SUDOX_ANDROID_BASE64_H
