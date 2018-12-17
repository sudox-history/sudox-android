package com.sudox.android.data.models.common

// TODO: Убрать разделение загрузок на типы для реализации нормальной поддержки библиотеки NoPaginate и упрощения логики.
enum class LoadingType {
    INITIAL,
    PAGING
}