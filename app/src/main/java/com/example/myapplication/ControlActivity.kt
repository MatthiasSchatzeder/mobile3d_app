package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.activity_control.toolbar


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
                    0 -> tab.setText("Axis")
                    1 -> tab.setText("Tool")
                    2 -> tab.setText("General")
                }
            }
        }).attach()

        //init toolbar
        var toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Control"

        /**
         * listens to clicks of the Navigation Icon
         */
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            var drawerLayout = drawer_layout
            drawerLayout.openDrawer(GravityCompat.START)
        })

        var navigationView = navigation_view
        /**
         * listens to clicks on the Navigation Viewer items
         */
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            if(item.itemId == R.id.item_control){

                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                return@setNavigationItemSelectedListener true
            }
            else if(item.itemId == R.id.item_printer){

                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                finish()
                //val intent = Intent(this, MainActivity::class.java)
                //startActivity(intent)

                return@setNavigationItemSelectedListener true
            }
            else if(item.itemId == R.id.item_settings){
                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)

                return@setNavigationItemSelectedListener true
            }

            return@setNavigationItemSelectedListener false
        }

    }


    /**
     * toolbar menu init
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.action_menu){
            Toast.makeText(this, "right icon pressed", Toast.LENGTH_SHORT).show()

            return true
        }


        return super.onOptionsItemSelected(item)
    }

}
