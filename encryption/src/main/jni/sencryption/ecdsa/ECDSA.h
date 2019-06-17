#ifndef SUDOX_ENCRYPTION_ECDSA_H
#define SUDOX_ENCRYPTION_ECDSA_H

/**
 * Проверяет данные на соответствие публичного ключа подписи.
 * Перед вызовом обязательно должен быть инициализирован верификатор.
 *
 * @param message - сообщение
 * @param messageLength - длина сообщения
 * @param signature - подпись сообщения
 * @param signatureLength - длина подписи сообщения
 */
bool verifyMessageWithECDSA(unsigned char *, unsigned int, unsigned char *, unsigned int);

/**
 * Читает публичный ключ, создает верификатора подписи с публичным ключем.
 */
void initECDSA();

#endif //SUDOX_ANDROID_ECDSA_H
