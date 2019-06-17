#ifndef SUDOX_ENCRYPTION_SHA_H
#define SUDOX_ENCRYPTION_SHA_H

#include <stdio.h>
#include <string>

using namespace std;

/**
 * Вычисляет SHA-224 хэш переданного массива байтов.
 *
 * @param data - массив байтов
 * @param length - длина массива байтов
 */
string calculateSHA224(unsigned char *, unsigned int);

#endif
