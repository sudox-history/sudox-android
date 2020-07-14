package ru.sudox.android.core.ui.avatar

import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import ru.sudox.android.core.ui.R

/**
 * Загружает аватарку.
 * Если id равен null, то показывает текстовую аватарку.
 *
 * @param fragment Фрагмент, с которого происходит загрузка
 * @param obj Обьект, аватар которого будет показан
 * @param text Текст, который будет отображен по центру текстовой аватарки
 * @param id ID фотографии, которая отображается как аватарка
 */
fun ImageView.loadAvatar(fragment: Fragment, obj: Any, text: String, id: String? = null) {
    if (id != null) {
        Glide.with(fragment)
            .load(id)
            .transition(withCrossFade(300))
            .placeholder(R.color.placeholderColor)
            .into(this)
    } else {
        showTextAvatar(fragment, obj, text)
    }
}

/**
 * Показывает текстовый аватар.
 *
 * @param fragment Фрагмент, с которого происходит загрузка
 * @param obj Обьект, аватар которого будет показан
 * @param text Текст, который будет отображен по центру.
 */
fun ImageView.showTextAvatar(fragment: Fragment, obj: Any, text: String) {
    val backgroundColors = context.resources.getIntArray(R.array.avatarsColors)
    val builder = StringBuilder()
    val words = text.split(' ')

    if (words.isNotEmpty()) {
        builder.append(words[0][0])
    }

    if (words.size >= 2) {
        builder.append(words[1][0])
    }

    val drawable = AvatarDrawable(builder.toString(), backgroundColors[obj.hashCode() % backgroundColors.size]) {
        it.color = context.getColor(R.color.avatarTextColor)
        it.typeface = ResourcesCompat.getFont(context, R.font.opensans_bold)
    }

    Glide.with(fragment)
        .load(drawable)
        .transition(withCrossFade(300))
        .placeholder(R.color.placeholderColor)
        .into(this)
}