package ru.sudox.android.people.common.vos

interface AvatarVO {
    fun getResourceId(): Long
    fun canShowIndicator(): Boolean
}