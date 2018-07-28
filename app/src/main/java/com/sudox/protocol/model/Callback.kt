package com.sudox.protocol.model

import com.sudox.protocol.model.dto.JsonModel
import kotlin.reflect.KClass

data class Callback(val modelClass: KClass<out JsonModel>, val callback: Any, val once: Boolean)