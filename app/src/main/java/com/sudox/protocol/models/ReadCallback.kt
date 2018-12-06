package com.sudox.protocol.models

import kotlin.coroutines.Continuation
import kotlin.reflect.KClass

class ReadCallback<T : JsonModel>(val resultFunction: ((T) -> (Unit))? = null,
                                  val coroutine: Continuation<T>? = null,
                                  val clazz: KClass<T>,
                                  val event: String? = null,
                                  val once: Boolean,
                                  val notifyAboutConnectionDestroyed: Boolean = false)

