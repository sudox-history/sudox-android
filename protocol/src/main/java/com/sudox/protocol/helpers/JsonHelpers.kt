package com.sudox.protocol.helpers

import org.json.JSONArray

/**
 * Метод, реализующий возможность итерации по JSON-массиву.
 */
fun JSONArray.asIterable(): Iterable<Any> {
    return object : Iterable<Any> {
        override fun iterator() = this@asIterable.iterator()
    }
}

/**
 * Создает итератор для JSON-массива.
 */
operator fun JSONArray.iterator() = object : Iterator<Any> {
    val it = (0 until this@iterator.length()).iterator()

    override fun next() = this@iterator.get( it.next())
    override fun hasNext() = it.hasNext()
}