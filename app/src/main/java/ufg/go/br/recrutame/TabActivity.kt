package ufg.go.br.recrutame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
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

class TabActivity : BaseActivity() {
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

        if (getMyPreferences().getIsNewUser()) {
            startShowCase()
        } else {
            bottonNavigationView.currentItem = 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = supportFragmentManager.fragments
        if (fragments != null) {
            for (f in fragments) {
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
        bottonNavigationView.currentItem = tab.value
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

    private fun startShowCase() {
        MaterialShowcaseView.resetSingleUse(this, CLIENT_ID)
        val config = ShowcaseConfig()
        config.delay = 500

        val sequence = MaterialShowcaseSequence(this, CLIENT_ID)
        sequence.setConfig(config)

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.activity_tab))
                .withoutShape()
                .setContentText(getString(R.string.welcome_app))
                .setDismissText(R.string.lets_go)
                .setDelay(1000)
                .build())

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.action_profile))
                .setContentText(getString(R.string.instruction_profile))
                .setDismissText(R.string.got_it)
                .build())

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.action_settings))
                .setContentText(R.string.instructions_settings)
                .setDismissText(R.string.got_it)
                .withRectangleShape()
                .build())

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.action_work))
                .setContentText(R.string.instructions_search)
                .setDismissText(R.string.got_it)
                .withRectangleShape()
                .build())

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.action_chat))
                .setContentText(R.string.instructions_chat)
                .setDismissText(R.string.got_it)
                .withRectangleShape()
                .build())

        sequence.start()
        sequence.setOnItemDismissedListener { materialShowcaseView, i ->
            when (i){
                0 -> EventBus.getDefault().post(EnumShowCase.PROFILE)
                1 -> EventBus.getDefault().post(EnumShowCase.SETTINGS)
                2 -> EventBus.getDefault().post(EnumShowCase.JOB)
                3 -> EventBus.getDefault().post(EnumShowCase.CHAT)
                4 -> finishShowCase()
            }
        }
    }

    private fun finishShowCase() {
        EventBus.getDefault().post(EnumShowCase.PROFILE)
        getMyPreferences().setIsNewUser(false)
    }
}