package com.example.myapplication


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
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
         * listens to clicks on the Navigation Viewer items
         */
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            if(item.itemId == R.id.item_control){

                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                //open control activity
                val intent = Intent(this, ControlActivity::class.java)
                startActivity(intent)

                return@setNavigationItemSelectedListener true
            }
            else if(item.itemId == R.id.item_printer){
                Toast.makeText(this, "Printer pressed", Toast.LENGTH_SHORT).show()

                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                return@setNavigationItemSelectedListener true
            }
            else if(item.itemId == R.id.item_settings){
                Toast.makeText(this, "Settings pressed", Toast.LENGTH_SHORT).show()

                return@setNavigationItemSelectedListener true
            }

            return@setNavigationItemSelectedListener false
        }

    }


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
