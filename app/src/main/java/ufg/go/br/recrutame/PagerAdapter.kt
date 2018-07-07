package ufg.go.br.recrutame
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter;
/**
 * Created by claud on 03/07/2018.
 */
class PagerAdapter(fragmentManager: FragmentManager, private val numOfTabs: Int) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val tab = when(position){
            1 -> ProfileFragment()
            2 -> JobFragment()
            else -> ChatFragment()
        }

        return tab;
    }

    override fun getCount(): Int {
        return numOfTabs;
    }
}