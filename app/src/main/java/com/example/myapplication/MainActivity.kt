package com.example.myapplication


import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*

var GlobalAuthToken = ""

class MainActivity : AppCompatActivity() {

    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init toolbar
        var toolbar = toolbar
        setSupportActionBar(toolbar)


        setStatusView(0)
        handler.postDelayed({
            setStatusView(1)
            handler.postDelayed({setStatusView(2) },3000)
        },3000)




        var ret = HttpClientConnect().execute("<ip of the raspberry / backend>").get()
        Log.e("test ", "return $ret")

        /**
         * listens to clicks of the Navigation Icon on the toolbar
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
            } else if (item.itemId == R.id.item_wifi) {
                var drawerLayout = drawer_layout
                drawerLayout.closeDrawers()

                val intent = Intent(this, BluetoothLeActivity::class.java)
                startActivity(intent)

                return@setNavigationItemSelectedListener true
            }

            return@setNavigationItemSelectedListener false
        }
    }//onCreate

    private fun setStatusView(status: Int){
        when(status){
            0->{
                textView_status.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorLightGrey))
                textView_status.text = "connecting..."
            }
            1->{
                textView_status.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorGreen))
                textView_status.text = "connected"
            }
            2->{
                textView_status.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorRed))
                textView_status.text = "offline"
            }
        }
    }

    /**
     * initializes sidebar menu layout
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    /**
     *listens to clicks on the right (more) icon on the toolbar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_menu) {
            Toast.makeText(this, "right icon pressed", Toast.LENGTH_SHORT).show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
