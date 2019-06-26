package com.sudox.protocol.client.serialization

import kotlin.reflect.KClass

abstract class Serializable {

    internal var paramsCounter = 0

    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable> readObjectParam(key: String, clazz: KClass<T>, params: LinkedHashMap<String, Any>): T? {
        val value = params[key] as? LinkedHashMap<*, *> ?: return null

        return clazz.java.newInstance().apply {
            deserialize(value as LinkedHashMap<String, Any>)
        }
    }

    abstract fun serialize(serializer: Serializer)
    abstract fun deserialize(params: LinkedHashMap<String, Any>)
}