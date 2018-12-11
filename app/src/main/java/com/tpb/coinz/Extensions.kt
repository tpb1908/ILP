package com.tpb.coinz

import android.app.Activity
import android.content.res.Configuration
import android.text.Editable
import android.text.TextWatcher


inline fun <R> R?.orElse(block: () -> R): R {
    return this ?: block()
}

fun Activity.isNightModeEnabled(): Boolean = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
        Configuration.UI_MODE_NIGHT_YES


abstract class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        p0?.let { onTextChanged(it.toString()) }
    }

    abstract fun onTextChanged(text: String)
}

