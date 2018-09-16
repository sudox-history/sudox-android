package com.sudox.protocol.model

import kotlin.reflect.KClass

class ReadCallback<T : JsonModel>(val resultFunction: ((T) -> (Unit)),
                                  val clazz: KClass<T>,
                                  val event: String,
                                  val once: Boolean)

