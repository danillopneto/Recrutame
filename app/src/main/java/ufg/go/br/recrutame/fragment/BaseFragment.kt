package ufg.go.br.recrutame.fragment

import android.app.AlertDialog
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.MyPreferences

abstract class BaseFragment : Fragment() {
    lateinit var mAuth: FirebaseAuth

    fun getMyPreferences(): MyPreferences {
        return StoreBox.create(context, MyPreferences :: class.java)
    }

    fun handleYesNoDialog(message: Int, yesAction: () -> Unit) {
        val li = LayoutInflater.from(context)
        val promptsView = li.inflate(R.layout.dialog_yes_no, null)
        promptsView.findViewById<TextView>(R.id.questionTxt).text = getString(message)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(promptsView)
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes)
                ) { dialog, id ->
                    yesAction()
                }
                .setNegativeButton(getString(R.string.no)
                ) { dialog, id -> dialog.cancel() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun inicializeApis() {
        mAuth = FirebaseAuth.getInstance()
    }
}
