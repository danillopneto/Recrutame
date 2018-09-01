package ufg.go.br.recrutame.activity.profile

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.activity.BaseActivity
import ufg.go.br.recrutame.util.CustomProgressBar
import android.support.annotation.NonNull



abstract class EditProfileActivity : BaseActivity() {
    protected lateinit var userId: String
    protected lateinit var mDatabase: DatabaseReference
    abstract var layoutId: Int
    private val progressBar = CustomProgressBar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance().reference
        userId = intent.getStringExtra("userId")
        showActionBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(getActionMenu(), menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { finish(); return true; }
            R.id.mMenuSaveProfile -> saveInfo()
        }

        return super.onOptionsItemSelected(item)
    }

    abstract fun saveInfo()

    open fun getActionMenu(): Int {
        return R.menu.menu_action_edit
    }

    open fun getActionBarTitle(): String {
        return getString(R.string.edit)
    }

    protected fun <T>setSpinnerConfig(spinner: Spinner, list: Array<T>, defaultValue: T) {
        val adapter = ArrayAdapter<T>(this, R.layout.custom_simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.background = ContextCompat.getDrawable(this, R.drawable.abc_edit_text_material)
        if (defaultValue != null) {
            val position = adapter.getPosition(defaultValue)
            spinner.setSelection(position)
        } else {
            spinner.setSelection(0)
        }

        adapter.notifyDataSetChanged()
    }

    protected fun handleYesNoDialog(message: Int, yesAction: () -> Unit) {
        val li = LayoutInflater.from(this)
        val promptsView = li.inflate(R.layout.dialog_yes_no, null)
        promptsView.findViewById<TextView>(R.id.questionTxt).text = getString(message)
        val alertDialogBuilder = AlertDialog.Builder(this)
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

    protected fun clearError(editText: EditText) {
        val textInputLayout = getTextInputLayout(editText)
        if (textInputLayout != null) {
            textInputLayout.isErrorEnabled = false
            textInputLayout.error = null
        } else {
            editText.error = null
        }
    }

    protected fun showError(editText: EditText, errorMessage: Int) {
        val textInputLayout = getTextInputLayout(editText)
        if (textInputLayout != null) {
            textInputLayout.error = getString(errorMessage)
        } else {
            editText.error = getString(errorMessage)
        }
    }

    protected fun showError(errorMessage: Int) {
        val snackBar = Snackbar.make(findViewById(layoutId), errorMessage, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.white))
        val group = snackBar.view as ViewGroup
        group.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        snackBar.show()
    }

    protected fun showProgressBar() {
        if (progressBar.dialog == null
                || !progressBar.dialog!!.isShowing) {
            progressBar.show(this)
        }
    }

    protected fun hideProgressBar() {
        progressBar.hide()
    }

    private fun getTextInputLayout(editText: EditText): TextInputLayout? {
        var currentView: View = editText
        for (i in 0..1) {
            val parent = currentView.parent
            if (parent is TextInputLayout) {
                return parent
            } else {
                currentView = parent as View
            }
        }
        return null
    }

    private fun showActionBar() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getActionBarTitle()
    }
}