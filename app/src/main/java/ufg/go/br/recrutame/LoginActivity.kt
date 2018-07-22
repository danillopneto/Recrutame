package ufg.go.br.recrutame

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.api.model.LIProfileInfo
import ufg.go.br.recrutame.model.MyPreferences

abstract class LoginActivity : AppCompatActivity() {
    lateinit var liProfileInfo: LIProfileInfo
    lateinit var mAuth: FirebaseAuth
    val profileUpdates = UserProfileChangeRequest.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inicializeApis()
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, TabActivity :: class.java))
        }
    }

    abstract fun getProgressBar(): ProgressBar

    fun handleLogin(email: String, password: String, isNewUser: Boolean, isLIUser: Boolean) {
        getMyPreferences().setIsNewUser(isNewUser)
        getMyPreferences().setIsLIUser(isLIUser)
        hideKeyboard()
        if (!email.isEmpty() && !password.isEmpty()) {
            getProgressBar().visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                getProgressBar().visibility = View.INVISIBLE
                if (!task.isSuccessful) {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseNetworkException) {
                        Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                        Log.e(TAG, e.message)
                    }
                } else {
                    this.finishAffinity()
                    getMyPreferences().setUserEmail(email)
                    startActivity(Intent(this, TabActivity :: class.java))
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.fill_credential), Toast.LENGTH_LONG).show()
        }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun setNewUserData(displayName: String, photoUri: String) {
        profileUpdates.setDisplayName(displayName)
        profileUpdates.setPhotoUri(Uri.parse(photoUri))
    }

    private fun inicializeApis() {
        mAuth = FirebaseAuth.getInstance()
    }

    private fun getMyPreferences(): MyPreferences {
        return StoreBox.create(applicationContext, MyPreferences::class.java)
    }
}
