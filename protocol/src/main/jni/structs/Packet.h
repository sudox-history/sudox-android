#ifndef SSOCKETS_DATA_H
#define SSOCKETS_DATA_H

#include <stddef.h>

struct Packet {
    char *buffer;
    size_t count;
    bool urgent;
};

#endif
