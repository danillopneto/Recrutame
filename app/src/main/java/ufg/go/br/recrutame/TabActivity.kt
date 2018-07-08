package ufg.go.br.recrutame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.MotionEvent
import android.view.View
import ufg.go.br.recrutame.adapter.PagerAdapter

class TabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tab_layout)

        var tabLayout = findViewById(R.id.tab_layout) as TabLayout;
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_account_circle));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_work));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_chat));
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL;

        var viewPager = findViewById(R.id.pager) as ViewPager;
        var pagerAdapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount);
        viewPager.adapter = pagerAdapter;


        viewPager.setOnTouchListener {v: View, m: MotionEvent ->
            true
        }

        viewPager.addOnPageChangeListener( TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }
}