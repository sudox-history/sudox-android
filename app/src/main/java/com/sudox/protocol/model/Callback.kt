package com.sudox.protocol.model

import com.sudox.protocol.model.dto.JsonModel
import kotlin.reflect.KClass

data class Callback<T : JsonModel>(val modelClass: KClass<T>, val callback: ResponseCallback<T>, val once: Boolean)