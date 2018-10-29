package com.sudox.android.common.helpers

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat.getColor
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import com.androidadvance.topsnackbar.TSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
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

fun hideKeyboard(context: Context, view: View) {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    // Hide keyboard & remove focus
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun convertDpToPixel(dp: Float, context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun convertPixelsToDp(px: Float, context: Context): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun drawAvatar(text: String, firstColor: String, secondColor: String): Bitmap {
    val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Draw gradient
    paint.shader = LinearGradient(0F, 0F, 200F, 200F, Color.parseColor(firstColor), Color.parseColor(secondColor), Shader.TileMode.REPEAT)

    // Draw circle
    canvas.drawRect(0F, 0F, 200F, 200F, paint)

    // Text bounds
    val textRect = Rect()

    // Draw text
    paint.shader = null
    paint.color = Color.WHITE
    paint.textSize = 60F
    paint.getTextBounds(text, 0, text.length, textRect)
    canvas.drawText(text, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

    return bitmap
}

fun ContextMenu.setOnItemClickListener(callback: (MenuItem) -> (Boolean)) {
    val size = size()

    // Установим слушатели
    for (i in 0 until size) {
        getItem(i).setOnMenuItemClickListener { callback(it) }
    }
}

fun drawCircleBitmap(context: Context, bitmap: Bitmap, view: ImageView) {
    Glide.with(context)
            .load(bitmap)
            .apply(RequestOptions.circleCropTransform())
            .into(view)
}

/**
 * Генерирует строку из 1-х букв имени и фамилии
 *
 * Например:
 * Полное имя: Максим Митюшкин
 * Короткое: ММ
 **/
fun String.getTwoFirstLetters(): String {
    val builder = StringBuilder()
    val names = split(" ")

    // Билдим имя
    if (names.isNotEmpty()) builder.append(names[0][0])
    if (names.size >= 2) builder.append(names[1][0])

    return builder.toString()
}