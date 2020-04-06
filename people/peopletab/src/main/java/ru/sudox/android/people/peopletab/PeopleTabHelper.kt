package ru.sudox.android.people.peopletab

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sudox.design.viewlist.ViewList

/**
 * Создает список с возможно знакомыми пользователями
 *
 * @param context Контекст приложения/активности
 * @return Возвращает список с уже определенными параметрами кроме адаптера
 */
fun createMaybeYouKnowRecyclerView(context: Context): ViewList {
    return ViewList(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
}