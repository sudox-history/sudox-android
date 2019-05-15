#ifndef SUDOX_ANDROID_JNIHELPER_H
#define SUDOX_ANDROID_JNIHELPER_H

#include <jni.h>

// Обьект JavaVM.
// Должен быть инициализирован для работы данного файла.
extern JavaVM *javaVM;

/**
 * Выдает обьект для работы с JVM.
 * Каждый обьект уникален для каждого потока.
 *
 * @param isThreadAttached - был ли подключен поток к контексту снова?
 */
JNIEnv* GetJavaEnv(bool &isThreadAttached);

#endif
