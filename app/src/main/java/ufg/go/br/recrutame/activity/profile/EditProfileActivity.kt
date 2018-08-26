package ufg.go.br.recrutame.activity.profile

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ufg.go.br.recrutame.R

abstract class EditProfileActivity : AppCompatActivity() {
    protected lateinit var userId: String
    protected lateinit var mDatabase: DatabaseReference
    abstract var layoutId: Int

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

    protected fun showError(errorMessage: Int) {
        val snackBar = Snackbar.make(findViewById(layoutId), errorMessage, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.white))
        val group = snackBar.view as ViewGroup
        group.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        snackBar.show()
    }

    open fun getActionMenu(): Int {
        return R.menu.menu_action_edit
    }

    open fun getActionBarTitle(): String {
        return getString(R.string.edit)
    }

    fun handleYesNoDialog(message: Int, yesAction: () -> Unit) {
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

    private fun showActionBar() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getActionBarTitle()
    }
}