package ufg.go.br.recrutame.adapter
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter;
import ufg.go.br.recrutame.fragment.ChatFragment
import ufg.go.br.recrutame.fragment.JobFragment
import ufg.go.br.recrutame.fragment.ProfileFragment

/**
 * Created by claud on 03/07/2018.
 */
class PagerAdapter(fragmentManager: FragmentManager, private val numOfTabs: Int) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val tab = when(position){
            0 -> ProfileFragment()
            1 -> JobFragment()
            else -> ChatFragment()
        }

        return tab;
    }

    override fun getCount(): Int {
        return numOfTabs;
    }
}