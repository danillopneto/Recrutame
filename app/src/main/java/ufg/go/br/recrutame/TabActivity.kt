package ufg.go.br.recrutame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import ufg.go.br.recrutame.fragment.ChatFragment
import ufg.go.br.recrutame.fragment.SettingsFragment
import ufg.go.br.recrutame.fragment.JobFragment

class TabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, MainActivity :: class.java))
            return
        }

        setContentView(R.layout.activity_tab)

        val bottonNavigationView: BottomNavigationViewEx = findViewById(R.id.navigation)
        bottonNavigationView.setIconSize(27f,27f)
        bottonNavigationView.setTextVisibility(false)
        bottonNavigationView.currentItem = 0
        bottonNavigationView.setOnNavigationItemSelectedListener(object: BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                loadFragment(item.itemId)
                return true
            }
        } )

        loadFragment(R.id.action_work)
    }

    private fun loadFragment(id: Int){
        var selectedFragment: Fragment? = null

        when (id) {
            R.id.action_work -> selectedFragment = JobFragment()
            R.id.action_chat -> selectedFragment = ChatFragment()
            R.id.action_profile -> selectedFragment = SettingsFragment()
            R.id.action_settings -> selectedFragment = SettingsFragment()
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, selectedFragment)
        transaction.commit()
    }
}