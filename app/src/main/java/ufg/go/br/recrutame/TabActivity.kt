package ufg.go.br.recrutame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.fragment.ChatFragment
import ufg.go.br.recrutame.fragment.SettingsFragment
import ufg.go.br.recrutame.fragment.JobFragment
import ufg.go.br.recrutame.fragment.ProfileFragment
import ufg.go.br.recrutame.model.MyPreferences

class TabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, MainActivity :: class.java))
            return
        } else {

        }

        setContentView(R.layout.activity_tab)

        val bottonNavigationView: BottomNavigationViewEx = findViewById(R.id.navigation)
        bottonNavigationView.setIconSize(27f,27f)
        bottonNavigationView.setTextVisibility(false)
        bottonNavigationView.enableItemShiftingMode(false)
        bottonNavigationView.enableShiftingMode(false)
        val preferences = StoreBox.create(applicationContext, MyPreferences::class.java)
        if (preferences.getIsNewUser()) {
            bottonNavigationView.currentItem = 3
        } else {
            bottonNavigationView.currentItem = 0
        }

        bottonNavigationView.setOnNavigationItemSelectedListener(object: BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                loadFragment(item.itemId)
                return true
            }
        } )

        loadFragment(R.id.action_work)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = supportFragmentManager.fragments
        if (fragments != null) {
            for (f in fragments!!) {
                if (f is ProfileFragment) {
                    f.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    private fun loadFragment(id: Int){
        var selectedFragment: Fragment? = null

        when (id) {
            R.id.action_work -> selectedFragment = JobFragment()
            R.id.action_chat -> selectedFragment = ChatFragment()
            R.id.action_profile -> selectedFragment = ProfileFragment()
            R.id.action_settings -> selectedFragment = SettingsFragment()
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, selectedFragment)
        transaction.commit()
    }
}