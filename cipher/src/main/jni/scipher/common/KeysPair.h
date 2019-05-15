#ifndef SUDOX_ANDROID_KEYSPAIR_H
#define SUDOX_ANDROID_KEYSPAIR_H

#include <crypto++/secblock.h>

struct KeysPair {
    CryptoPP::SecByteBlock keyPrivate;
    CryptoPP::SecByteBlock keyPublic;
};

#endif //SUDOX_ANDROID_KEYSPAIR_H
