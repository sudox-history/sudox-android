package ru.sudox.android.countries.impl.shadows

import android.content.res.Resources
import androidx.annotation.BoolRes
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

val BOOL_TABLE = HashMap<Int, Boolean>()

@Implements(Resources::class)
class ResourcesMock {

    @Implementation
    fun getBoolean(@BoolRes id: Int): Boolean = BOOL_TABLE[id] ?: true
}