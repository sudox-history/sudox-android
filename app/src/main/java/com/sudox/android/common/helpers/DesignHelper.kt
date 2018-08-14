package com.sudox.android.common.helpers

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import com.androidadvance.topsnackbar.TSnackbar
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

fun showTopSnackbar(context: Context, container: View, message: String, length: Int): TSnackbar {
    val snackbar = TSnackbar.make(container, message, length)

    // Change background color & show
    snackbar.view.setBackgroundColor(getColor(context, R.color.colorPrimary))
    snackbar.show()

    return snackbar
}


@Suppress("DEPRECATION")
fun formatHtml(string: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(string)
    }
}