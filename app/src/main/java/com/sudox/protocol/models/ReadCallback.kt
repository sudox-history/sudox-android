package com.sudox.protocol.models

import kotlin.reflect.KClass

class ReadCallback<T : JsonModel>(val resultFunction: ((T) -> (Unit)),
                                  val clazz: KClass<T>,
                                  val event: String? = null,
                                  val once: Boolean)

