#ifndef SSOCKETS_JNICALLBACK_H
#define SSOCKETS_JNICALLBACK_H

#include "SocketCallback.h"

#include <jni.h>

class JNICallback : public SocketCallback {
private:
    jobject javaReference; // Reference for Java's callback
    jclass javaClass;
    jmethodID onConnectedMethod;
    jmethodID onDisconnectedMethod;
    jmethodID onReceivedMethod;
public:
    JNICallback(JNIEnv *env, jobject javaInstance);

    ~JNICallback();

    void onConnected() override;

    void onDisconnected(bool error) override;

    void onReceived() override;
};

#endif
