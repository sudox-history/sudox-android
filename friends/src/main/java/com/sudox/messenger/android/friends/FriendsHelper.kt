package com.sudox.messenger.android.friends

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.viewlist.ViewList
import com.sudox.messenger.android.friends.adapters.MaybeYouKnowAdapter

/**
 * Создает карусель с возможно знакомыми друзьями
 *
 * @param context Контекст приложения/активности
 * @return Необходимый для работы ViewList с уже заданным адаптером и менеджером разметки
 */
fun createMaybeYouKnowList(context: Context): ViewList {
    return ViewList(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = MaybeYouKnowAdapter(this)
    }
}