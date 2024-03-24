package com.app.news.utils

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {

    fun showSnackbarShort(view: View, message: String?) {
        if (message.isNullOrBlank()) return
        Snackbar.make(view, message.trim(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }


    fun showSnackbarLong(view: View, message: String?) {
        if (message.isNullOrBlank()) return
        Snackbar.make(view, message.trim(), BaseTransientBottomBar.LENGTH_LONG).show()
    }
}