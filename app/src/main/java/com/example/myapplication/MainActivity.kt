package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init toolbar
        var toolbar = toolbar
        setSupportActionBar(toolbar)


        /**
         * listens to clicks of the Navigation Icon
         */
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            var drawerLayout = drawer_layout
            drawerLayout.openDrawer(GravityCompat.START)
        })

        var navigationView = navigation_view
        /**
         * listens to clicks on the Navigation Viewer (sidebar) items
         */
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.item_control) {

                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                //open control activity
                val intent = Intent(this, ControlActivity::class.java)
                startActivity(intent)

                return@setNavigationItemSelectedListener true
            } else if (item.itemId == R.id.item_printer) {
                Toast.makeText(this, "Printer pressed", Toast.LENGTH_SHORT).show()

                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                return@setNavigationItemSelectedListener true
            } else if (item.itemId == R.id.item_settings) {
                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)

                return@setNavigationItemSelectedListener true
            }

            return@setNavigationItemSelectedListener false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_menu) {
            Toast.makeText(this, "right icon pressed", Toast.LENGTH_SHORT).show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }


}
