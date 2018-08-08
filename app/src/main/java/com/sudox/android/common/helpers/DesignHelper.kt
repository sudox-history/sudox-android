package com.sudox.android.common.helpers

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.sudox.android.R

fun showInputError(inputLayout: TextInputLayout) {
    inputLayout.error = " "

    if (inputLayout.childCount == 2) {
        (inputLayout.getChildAt(1) as ViewGroup)
                .getChildAt(0)
                .visibility = View.GONE
    }
}

fun hideInputError(inputLayout: TextInputLayout) {
    inputLayout.isErrorEnabled = false
}

fun showSnackbar(context: Context, container: View, message: String, length: Int): Snackbar {
    val snackbar = Snackbar.make(container, message, length)

    // Change background color & show
    snackbar.view.setBackgroundColor(getColor(context, R.color.colorPrimary))
    snackbar.show()

    return snackbar
}