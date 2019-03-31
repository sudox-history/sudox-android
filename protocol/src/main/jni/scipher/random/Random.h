#ifndef SUDOX_ANDROID_RANDOM_H
#define SUDOX_ANDROID_RANDOM_H

#include <stdio.h>
#include <crypto++/secblock.h>
#include <crypto++/randpool.h>

// Пул генератора случайных чисел.
// Инициализируется после вызова initRandom()
static CryptoPP::RandomPool randomPool;

// Количество байтов, которые будут взяты с /dev/urandom в качестве энтропии
static unsigned int ENTROPY_SIZE = 1024;

/**
 * Генерирует энтропию, создает пул генератора случайных чисел.
 */
void initRandom();

/**
 * Генерирует блок случайных байтов.
 * Перед вызовом обязательно должен быть инициализирован пул.
 *
 * @param length - длина массива.
 */
CryptoPP::SecByteBlock generateBytes(size_t length);

#endif //SUDOX_ANDROID_RANDOM_H