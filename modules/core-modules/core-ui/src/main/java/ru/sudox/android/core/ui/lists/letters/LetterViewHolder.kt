package ru.sudox.android.core.ui.lists.letters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.sudox.android.core.ui.R
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для липкой буквы
 * TODO: Добавить возможность клика по View
 *
 * @param view View, которая будет внутри ViewHolder'а
 */
class LetterViewHolder(
    view: View
) : BasicListHolder<LetterViewObject>(view) {

    override fun bind(item: BasicListItem<LetterViewObject>, changePayload: List<Any>?) {
        val textView = itemView as TextView
        val letter =  item.viewObject!!.char

        if (textView.text.isEmpty() || textView.text[0] != letter[0]) {
            textView.text = letter
        }
    }
}

/**
 * Создает ViewHolder для липкой буквы
 *
 * @param inflater LayoutInflater для получения разметки
 * @param parent Родительская View
 * @return ViewHolder для липкой буквы
 */
fun createLetterViewHolder(inflater: LayoutInflater, parent: ViewGroup): LetterViewHolder =
    LetterViewHolder(inflater.inflate(R.layout.item_sticky_letter, parent, false))