#include <jni.h>

#include "../helpers/JNIHelper.h"
#include "JNICallback.h"

JNICallback::JNICallback(JNIEnv* env, jobject javaInstance) {
    this->javaReference = env->NewGlobalRef(javaInstance);
    this->javaClass = env->GetObjectClass(this->javaReference);
    this->onConnectedMethod = env->GetMethodID(this->javaClass, "socketConnected", "()V");
    this->onDisconnectedMethod = env->GetMethodID(this->javaClass, "socketClosed", "(Z)V");
    this->onReceivedMethod = env->GetMethodID(this->javaClass, "socketReceive", "()V");
}

JNICallback::~JNICallback() {
    bool isThreadAttached = false;
    JNIEnv *env = GetJavaEnv(isThreadAttached);
    env->DeleteGlobalRef(this->javaReference);

    // Detach thread if needed
    if (isThreadAttached) {
        javaVM->DetachCurrentThread();
    }
}

void JNICallback::onConnected() {
    bool isThreadAttached = false;
    JNIEnv *env = GetJavaEnv(isThreadAttached);
    env->CallVoidMethod(this->javaReference, this->onConnectedMethod);

    if (isThreadAttached) {
        javaVM->DetachCurrentThread();
    }
}

void JNICallback::onDisconnected(bool error) {
    bool isThreadAttached = false;
    JNIEnv *env = GetJavaEnv(isThreadAttached);
    env->CallVoidMethod(this->javaReference, this->onDisconnectedMethod, error);

    if (isThreadAttached) {
        javaVM->DetachCurrentThread();
    }
}

void JNICallback::onReceived() {
    bool isThreadAttached = false;
    JNIEnv *env = GetJavaEnv(isThreadAttached);
    env->CallVoidMethod(this->javaReference, this->onReceivedMethod);

    if (isThreadAttached) {
        javaVM->DetachCurrentThread();
    }
}
