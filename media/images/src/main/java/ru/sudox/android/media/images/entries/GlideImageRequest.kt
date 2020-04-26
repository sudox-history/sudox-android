package ru.sudox.android.media.images.entries

/**
 * Обьект для запроса загрузки изображения.
 * Передается обработчикам Glide.
 *
 * @param imageId ID изображения.
 */
data class GlideImageRequest(
        val imageId: Long
)