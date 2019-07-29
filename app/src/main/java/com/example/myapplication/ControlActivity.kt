package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_control.*

class ControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)


        var adapter = FragmentPagerAdapter(supportFragmentManager, lifecycle)

        adapter.addFragment(Axes())
        adapter.addFragment(Tool())
        adapter.addFragment(General())

        viewpager.adapter = adapter

        TabLayoutMediator(tabs, viewpager, object: TabLayoutMediator.OnConfigureTabCallback{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position){
                    0 -> tab.setText("Axes")
                    1 -> tab.setText("Tool (E)")
                    2 -> tab.setText("General")
                }
            }
        }).attach()


        /*
        viewpager.adapter = ViewPagerAdapter()

        TabLayoutMediator(tabs, viewpager, object: TabLayoutMediator.OnConfigureTabCallback{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position){
                    0 -> tab.setText("Axes")
                    1 -> tab.setText("Tool (E)")
                    2 -> tab.setText("General")
                }
            }
        }).attach()
        */
    }
}
