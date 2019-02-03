package com.sudox.android.common.livedata

import com.sudox.protocol.models.SingleLiveEvent
import java.util.*

class ActiveSingleLiveEvent<T> : SingleLiveEvent<T>() {

    private val values: Queue<T> = LinkedList()
    private var isActive: Boolean = false
    var queueFilter: ((T, T?) -> (Boolean))? = null

    override fun onActive() {
        isActive = true

        while (values.isNotEmpty()) {
            value = values.poll()
        }
    }

    override fun onInactive() {
        isActive = false
    }

    override fun setValue(t: T?) {
        if (isActive) {
            super.setValue(t)
        } else if (values.lastOrNull() != t) {
            // Удаляем все последние элементы, которые одобряет данные фильтр (чистка от неактуальных и ненужных данных)
            if (queueFilter != null) {
                values.removeAll { queueFilter!!(it, t) }
            }

            values.add(t)
        }
    }
}