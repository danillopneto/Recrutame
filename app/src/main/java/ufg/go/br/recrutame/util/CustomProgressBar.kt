package ufg.go.br.recrutame.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import ufg.go.br.recrutame.R

class CustomProgressBar {
    var dialog: Dialog? = null

    @JvmOverloads
    fun show(context: Context, cancelable: Boolean = false,
             cancelListener: DialogInterface.OnCancelListener? = null): Dialog {
        val inflator = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflator.inflate(R.layout.progress_bar, null)

        dialog = Dialog(context, R.style.NewDialog)
        dialog!!.setContentView(view)
        dialog!!.setCancelable(cancelable)
        dialog!!.setOnCancelListener(cancelListener)
        dialog!!.show()

        return dialog!!
    }

    fun hide() {
        dialog?.hide()
        dialog?.dismiss()
    }
}