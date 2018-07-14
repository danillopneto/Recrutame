package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.markushi.ui.CircleButton
import com.google.firebase.auth.FirebaseAuth
import ufg.go.br.recrutame.BuildConfig
import ufg.go.br.recrutame.MainActivity
import ufg.go.br.recrutame.R

class HomeFragment : Fragment(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<CircleButton>(R.id.settingsBtn).setOnClickListener(this)
        view.findViewById<CircleButton>(R.id.editProfileBtn).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.settingsBtn -> handleSettings()
            R.id.editProfileBtn -> handleProfileEdit()
        }
    }

    private fun handleProfileEdit() {

    }

    private fun handleSettings() {
        handleLogout()
    }

    private fun handleLogout() {
        val prefs = activity!!.application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        mAuth.signOut()
        activity!!.finishAffinity()
        val intent = Intent(activity!!.application, MainActivity :: class.java)
        startActivity(intent)
    }
}
