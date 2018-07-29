package ufg.go.br.recrutame

import android.content.Intent
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.fragment.UserPhotoFragment
import ufg.go.br.recrutame.model.MyPreferences

class IntroActivity : AppIntro2() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val welcomePage = SliderPage()
        welcomePage.title = getString(R.string.welcome_to_app)
        welcomePage.imageDrawable = R.drawable.icon
        welcomePage.bgColor = getColor(R.color.primaryColor)
        addSlide(AppIntroFragment.newInstance(welcomePage))

        addSlide(UserPhotoFragment())

        val donePage = SliderPage()
        donePage.title = getString(R.string.everything_set)
        donePage.imageDrawable = R.drawable.ic_checklist
        donePage.bgColor = getColor(R.color.purple_700)
        addSlide(AppIntroFragment.newInstance(donePage))

        showSkipButton(false)
        isProgressButtonEnabled = true

        setFlowAnimation()
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        finishAffinity()
        StoreBox.create(this, MyPreferences::class.java).setIsNewUser(false)
        startActivity(Intent(this, TabActivity :: class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = supportFragmentManager.fragments
        if (fragments != null) {
            for (f in fragments!!) {
                if (f is UserPhotoFragment) {
                    f.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }
}