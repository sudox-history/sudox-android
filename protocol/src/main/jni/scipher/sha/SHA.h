#ifndef SUDOX_ANDROID_SHA_H
#define SUDOX_ANDROID_SHA_H

#include <stdio.h>
#include <string>

/**
 * Вычисляет SHA-224 хэш переданного массива байтов.
 *
 * @param data - массив байтов
 * @param length - длина массива байтов
 */
std::string calculateSHA224(unsigned char *, unsigned int);

#endif //SUDOX_ANDROID_SHA_H
