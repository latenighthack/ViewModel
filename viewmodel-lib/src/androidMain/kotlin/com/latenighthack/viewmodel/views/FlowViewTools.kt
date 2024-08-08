package com.latenighthack.viewmodel.views

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

public fun <T: EditText> T.onChanged(callback: (String) -> Unit): T {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(string: Editable?) {
        }

        override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {
            callback(string?.toString() ?: "")
        }
    })

    return this
}

public fun <T: EditText> T.onChanged(scope: CoroutineScope, callback: suspend (String) -> Unit): T {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(string: Editable?) {
        }

        override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {
            scope.launch {
                callback(string?.toString() ?: "")
            }
        }
    })

    return this
}

public fun <T: View> T.onClick(scope: CoroutineScope, callback: suspend () -> Unit): T {
    return this.onClickInternal(scope, callback, true)
}

public fun <T: View> T.onClickNoSpinner(scope: CoroutineScope, callback: suspend () -> Unit): T {
    return this.onClickInternal(scope, callback, false)
}

public fun Activity.showDialog(title: String, text: String) {
    AlertDialog.Builder(this)
        .setMessage(title)
        .setMessage(text)
        .setPositiveButton("OK") { _, _ -> }
        .show()
}

private fun <T: View> T.onClickInternal(scope: CoroutineScope, callback: suspend () -> Unit, showSpinner: Boolean = true): T {
    setOnClickListener {
        var progress: ProgressDialog? = null

        fun doShowSpinner() {
            progress = ProgressDialog(context)

            progress?.setTitle("Loading")

            progress?.setCancelable(false)

            progress?.show()
        }

        var alreadyComplete = false

        scope.launch {
            try {
                this@onClickInternal.activity?.runOnUiThread {
                    if (showSpinner && !alreadyComplete) {
                        doShowSpinner()
                    }
                }

                callback()

                alreadyComplete = true
            } catch (ex: Throwable) {
                this@onClickInternal.activity?.runOnUiThread {
                    this@onClickInternal.activity?.showDialog("Error", ex.toString())

                    // throw ex
                }
            }

            this@onClickInternal.activity?.runOnUiThread {
                progress?.dismiss()
            }
        }
    }

    return this
}

public fun <T: View> T.onClick(callback: () -> Unit): T {
    setOnClickListener {
        callback()
    }

    return this
}
