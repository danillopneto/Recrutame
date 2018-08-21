package ufg.go.br.recrutame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.UploadTask

class EditGeneralInfoActivity : AppCompatActivity() {
    private lateinit var userId: String
    private lateinit var mDatabase: DatabaseReference

    private lateinit var mNameTxt: EditText
    private lateinit var mLastNameTxt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_general_info)
        showActionBar()

        mDatabase = FirebaseDatabase.getInstance().reference

        userId = intent.getStringExtra("userId")

        val name = intent.getStringExtra("userName")
        mNameTxt = findViewById(R.id.mNameTxt)
        mNameTxt.setText(name)

        val lastName = intent.getStringExtra("userLastName")
        mLastNameTxt = findViewById(R.id.mLastNameTxt)
        mLastNameTxt.setText(lastName)
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

    private fun showActionBar() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.edit)
    }

    private fun saveGeneralInfo() {
        val generalInfo = mDatabase.child("users/$userId/generalInfo")
        generalInfo.child("name").setValue(mNameTxt.text.toString())
        generalInfo.child("lastName").setValue(mLastNameTxt.text.toString())
        finish()
    }
}
