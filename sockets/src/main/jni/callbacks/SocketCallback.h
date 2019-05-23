#ifndef SSOCKETS_SOCKETCALLBACK_H
#define SSOCKETS_SOCKETCALLBACK_H

#include <string>

using namespace std;

class SocketCallback {
public:
    virtual ~SocketCallback() {};
    virtual void onConnected() = 0;
    virtual void onDisconnected(bool error) = 0;
    virtual void onReceived() = 0;
};

#endif