package com.sudox.common.structures

class QueueList<T> {

    private var firstNode: QueueNode<T>? = null
    private var lastNode: QueueNode<T>? = null
    private var size: Int = 0

    fun push(element: T) {
        val node = getNode()

        if (node.element == null) {
            node.element = element
        } else {
            node.head = QueueNode()
            node.head!!.element = element
            lastNode = node.head
        }

        size++
    }

    fun shift(): T? {
        if (firstNode == null) {
            return null
        }

        val element = firstNode!!.element
        firstNode = firstNode!!.head
        size--

        return element
    }

    fun size(): Int {
        return size
    }

    private fun getNode(): QueueNode<T> {
        if (firstNode == null) {
            firstNode = QueueNode()
            lastNode = firstNode
        }

        return lastNode!!
    }
}