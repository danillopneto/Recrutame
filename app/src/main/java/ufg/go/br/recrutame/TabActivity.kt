package ufg.go.br.recrutame

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.squareup.picasso.Picasso
import net.orange_box.storebox.StoreBox
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ufg.go.br.recrutame.enum.EnumShowCase
import ufg.go.br.recrutame.fragment.ChatFragment
import ufg.go.br.recrutame.fragment.SettingsFragment
import ufg.go.br.recrutame.fragment.JobFragment
import ufg.go.br.recrutame.fragment.ProfileFragment
import ufg.go.br.recrutame.model.MyPreferences
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig



class TabActivity : AppCompatActivity() {
    private lateinit var bottonNavigationView: BottomNavigationViewEx

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, MainActivity :: class.java))
            return
        }

        setContentView(R.layout.activity_tab)

        bottonNavigationView = findViewById(R.id.navigation)
        bottonNavigationView.setIconSize(27f,27f)
        bottonNavigationView.setTextVisibility(false)
        bottonNavigationView.enableItemShiftingMode(false)
        bottonNavigationView.enableShiftingMode(false)
        bottonNavigationView.onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            loadFragment(item.itemId)
            true
        }

        bottonNavigationView.currentItem = 0
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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowCaseDone(tab: EnumShowCase) {
        when (tab) {
            EnumShowCase.PROFILE -> { bottonNavigationView.currentItem = EnumShowCase.SETTINGS.value }
            EnumShowCase.SETTINGS -> { bottonNavigationView.currentItem = EnumShowCase.JOB.value }
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