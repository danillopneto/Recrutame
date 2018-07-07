package ufg.go.br.recrutame

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter;
import ufg.go.br.recrutame.enum.EnumTabs

class PagerAdapter(fragmentManager: FragmentManager, private val numOfTabs: Int) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            EnumTabs.PROFILE.indice -> ProfileFragment()
            EnumTabs.JOBS.indice -> JobFragment()
            else -> ChatFragment()
        }
    }

    override fun getCount(): Int {
        return numOfTabs
    }
}