#include <helpers/JNIHelper.h>
#include <jni.h>

JavaVM *javaVM;

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, __unused void *unused) {
    javaVM = vm;

    // All successfully initialized!
    return JNI_VERSION_1_6;
}

JNIEnv *GetJavaEnv(bool &isThreadAttached) {
    JNIEnv *res;

    if (javaVM->GetEnv((void **) &res, JNI_VERSION_1_6) == JNI_EDETACHED) {
        javaVM->AttachCurrentThread(&res, nullptr);
        isThreadAttached = true;
    }

    return res;
}