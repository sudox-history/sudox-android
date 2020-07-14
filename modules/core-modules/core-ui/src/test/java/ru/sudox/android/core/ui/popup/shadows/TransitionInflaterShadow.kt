package ru.sudox.android.core.ui.popup.shadows

import android.transition.Transition
import android.transition.TransitionInflater
import androidx.annotation.TransitionRes
import org.mockito.Mockito
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

val TRANSITIONS_MAPPINGS = HashMap<@TransitionRes Int, Transition>()

/**
 * Mock-класс для компоновщика переходов
 */
@Implements(TransitionInflater::class)
class TransitionInflaterShadow {

    @Implementation
    fun inflateTransition(@TransitionRes resource: Int): Transition {
        return TRANSITIONS_MAPPINGS[resource] ?: Mockito.mock(Transition::class.java)
    }
}