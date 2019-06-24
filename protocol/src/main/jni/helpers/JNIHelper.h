#ifndef SSOCKETS_JNIHELPER_H
#define SSOCKETS_JNIHELPER_H

#include <jni.h>

extern JavaVM *javaVM;

JNIEnv *GetJavaEnv(bool &isThreadAttached);

#endif
