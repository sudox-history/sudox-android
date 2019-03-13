package com.sudox.android.common.helpers.livedata

import java.util.*

/**
 * LiveData, умеющая хранить данные, когда слушатель спит.
 *
 * Прочие возможности:
 *
 * 1) Поддержание актуальности данных по заданному фильтру.
 * 2) Возможность отправки множества данных. Данные не будут пропадать как в обычной LiveData.
 */
class ActiveSingleLiveEvent<T> : SingleLiveEvent<T>() {

    private val valuesQueue: Queue<T> = LinkedList()
    private var isActive: Boolean = false

    // Фильтр для удаления неактуальных данных.
    var filter: ((T, T?) -> (Boolean))? = null

    override fun setValue(t: T?) {
        // Методы onActive() и setValue() выполняются в одном потоке
        // => можно отбросить необходимость синхронизации данных
        if (!isActive) {
            if (filter != null) {
                valuesQueue.removeAll { filter!!(it, t) }
            }

            // Исключаем дублирование данных в очереди => уменьшаем кол-во ненужных обновлений в UI
            if (valuesQueue.find { it == t } == null) {
                valuesQueue.plusAssign(t)
            }
        } else {
            super.setValue(t)
        }
    }

    override fun onActive() {
        isActive = true

        // Отправляем недоставленные значения.
        while (valuesQueue.isNotEmpty()) {
            value = valuesQueue.poll()
        }
    }

    override fun onInactive() {
        isActive = false
    }
}