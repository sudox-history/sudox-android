package com.sudox.android.ui.common

import android.os.Bundle
import android.view.View
import dagger.android.support.DaggerFragment

/**
 * Позволяет переносить фрагменты в ледниковый период по желанию царя Activity
 * **/
abstract class FreezableFragment : DaggerFragment() {

    abstract fun freeze()
    abstract fun unfreeze()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Мы утилизируем фрагменты, поэтому при запуске нужна разморозка
        unfreeze()
    }
}