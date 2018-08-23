package ufg.go.br.recrutame

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

abstract class ProfileInfoActivity : AppCompatActivity() {
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
        menuInflater.inflate(R.menu.menu_action_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { finish(); return true; }
            R.id.mMenuSaveProfile -> saveGeneralInfo()
        }

        return super.onOptionsItemSelected(item)
    }

    abstract fun saveGeneralInfo()

    protected fun showError(errorMessage: Int) {
        val snackBar = Snackbar.make(findViewById(layoutId), errorMessage, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.white))
        val group = snackBar.view as ViewGroup
        group.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        snackBar.show()
    }

    private fun showActionBar() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.edit)
    }
}