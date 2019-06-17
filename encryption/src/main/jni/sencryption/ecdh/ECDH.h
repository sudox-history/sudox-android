#ifndef SUDOX_ANDROID_ECDH_H
#define SUDOX_ANDROID_ECDH_H

#include <scipher/common/KeysPair.h>

using namespace CryptoPP;

/**
 * Производит расчет секретного ключа на основе приватного ключа и публичного ключа собеседника.
 *
 * @param privateKey - приватный ключ
 * @param recipientPublicKey - публичный ключ собеседника.
 */
SecByteBlock calculateSecretKey(unsigned char *, unsigned char *);

/**
 * Генерирует пару ключей (приватный и публичный) и выдает их ID в таблице в качестве результата.
 */
unsigned int generateKeysPair();

/**
 * Ищет пару ключей по её ID.
 *
 * @param pairId - ID пары, полученный в ходе добавления записи.
 */
KeysPair *findKeyPair(unsigned int);

/**
 * Выдает приватный ключ из хранилища по ID его пары.
 *
 * @param pairId - ID пары, полученный в ходе добавления записи.
 */
SecByteBlock *getPrivateKey(unsigned int);

/**
 * Выдает публичный ключ из хранилища по ID его пары.
 *
 * @param pairId - ID пары, полученный в ходе добавления записи.
 */
SecByteBlock *getPublicKey(unsigned int);

/**
 * Удаляет все пары ключей из хранилища.
 */
void removeAllKeysPairs();

/**
 * Удаляет пару ключей из хранилища.
 */
void removeKeysPair(unsigned int);

#endif //SUDOX_ANDROID_ECDH_H
