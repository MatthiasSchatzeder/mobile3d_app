package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_control.*

/**
 * @myVib is used to make a physical haptic engine feedback on control button press
 */
var myVib: Vibrator? = null

class ControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        //init toolbar
        val toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Control"

        /**
         * listens to clicks of the Navigation Icon
         */
        toolbar.setNavigationOnClickListener {
            finish()
        }

        /**
         * create new adapter
         * documentation of FragmentPagerAdapter -> see implementation
         */
        val adapter = FragmentPagerAdapter(supportFragmentManager, lifecycle)

        /**
         * add fragments to the adapter
         */
        adapter.addFragment(Axis())
        adapter.addFragment(Tool())
        adapter.addFragment(General())

        /**
         * set the adapter of the viewpager
         */
        viewpager.adapter = adapter

        /**
         * TabLayoutMediator is used to combine the TabLayout and the viewpager -> so that they can be used together
         * TabLayoutMediator was copied and from github -> see class file for documentation
         *
         * onConfigureTab configures the tabs of the TabLayout
         * -> see TabLayoutMediator for detailed documentation
         */
        TabLayoutMediator(tabs, viewpager, object: TabLayoutMediator.OnConfigureTabCallback{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position){
                    0 -> tab.text = "Axis"
                    1 -> tab.text = "Tool"
                    2 -> tab.text = "General"
                }
            }
        }).attach()


        /**
         * initialize myVib with the system service Vibrator_Service
         */
        myVib = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }// onCreate


    /**
     * toolbar menu init
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }


    /**
     * on toolbar menu item clicked
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_menu){
            Toast.makeText(this, "right icon pressed", Toast.LENGTH_SHORT).show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
