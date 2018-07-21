package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.ramotion.fluidslider.FluidSlider
import ufg.go.br.recrutame.BuildConfig
import ufg.go.br.recrutame.MainActivity
import ufg.go.br.recrutame.R

class SettingsFragment : BaseFragment(), View.OnClickListener {
    private lateinit var maximumDistanceSlider: FluidSlider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        inicializeApis()
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        inicializeControls(view)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.logoutBtn -> handleLogout()
        }
    }

    private fun inicializeControls(view: View) {
        view.findViewById<Button>(R.id.logoutBtn).setOnClickListener(this)
        maximumDistanceSlider = view.findViewById(R.id.maximumDistanceSlider)
        maximumDistanceSlider.position = preferences.getMaximumDistance().toFloat()
        maximumDistanceSlider.positionListener = { p -> preferences.setMaximumDistance(p.toString()) }
    }

    private fun handleLogout() {
        preferences.clear()
        mAuth.signOut()
        activity!!.finishAffinity()
        val intent = Intent(activity!!.application, MainActivity :: class.java)
        startActivity(intent)
    }
}
