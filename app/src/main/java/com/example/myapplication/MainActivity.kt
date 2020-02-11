package com.example.myapplication


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*

/**
 * [maybe not necessary]
 * @GlobalAuthToken: global Variable with the auth bearer token needed for the socketIO connection / authentication
 *
 * @myIOSocket: SocketIO object that is used to communicate with the backend
 *
 * [maybe not necessary]
 * @BackendIP: String with the BackendIP (can be set manually or automatically by connecting the raspberry to a wifi via BLE)
 */
var GlobalAuthToken = ""
var MyIOSocket: Socket? = null
var BackendIP: String = ""

/**
 * SharedPref object to access the local stored shared preferences data
 */
var SharedPref: SharedPreferences? = null

class MainActivity : AppCompatActivity() {

    private val handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * init toolbar
         */
        val toolbar = toolbar
        setSupportActionBar(toolbar)

        /**
         * create a sharedPref object of the "mobile3d.preferences_ip" file
         */
        SharedPref = getSharedPreferences("mobile3d.preferences_ip", MODE_PRIVATE)
        
        setLoading(true)

        /**
         * call function to get socketIO connection
         *
         * delay 100ms to achieve that the loading animation shows up
         *      if the getIOSocketConnection is called directly the sharedPreference getString function needs some time
         *      and freezes the UI / activity
         */
        handler.postDelayed({
            getIOSocketConnection()
        }, 100)


        /**
         * listens to clicks of the Navigation Icon on the toolbar
         */
        toolbar.setNavigationOnClickListener {
            val drawerLayout = drawer_layout
            drawerLayout.openDrawer(GravityCompat.START)
        }


        /**
         * listens to clicks on the Navigation Viewer (sidebar) items
         */
        navigation_view.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.item_control -> {

                    val drawerLayout = drawer_layout
                    drawerLayout.closeDrawers()

                    //open control activity
                    val intent = Intent(this, ControlActivity::class.java)
                    startActivity(intent)

                    return@setNavigationItemSelectedListener true
                }
                R.id.item_printer -> {
                    Toast.makeText(this, "Printer pressed", Toast.LENGTH_SHORT).show()

                    val drawerLayout = drawer_layout
                    drawerLayout.closeDrawers()

                    return@setNavigationItemSelectedListener true
                }
                R.id.item_settings -> {
                    val drawerLayout = drawer_layout
                    drawerLayout.closeDrawers()

                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)

                    return@setNavigationItemSelectedListener true
                }
                R.id.item_wifi -> {
                    val drawerLayout = drawer_layout
                    drawerLayout.closeDrawers()

                    val intent = Intent(this, BluetoothLeActivity::class.java)
                    startActivity(intent)

                    return@setNavigationItemSelectedListener true
                }
                R.id.item_scoket -> {
                    val drawerLayout = drawer_layout
                    drawerLayout.closeDrawers()

                    val intent = Intent(this, SocketSetupActivity::class.java)
                    startActivity(intent)

                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener false
            }
        }

    }//onCreate

    /**
     * function that calls the ConnectSocket async task and defines the variable myIOSocket if
     * the task was successful.
     */
    private fun getIOSocketConnection(){
        val ip = SharedPref!!.getString("ip", "")

        if(ip!!.isNotEmpty()){

            val ret = ConnectSocketAsyncTask().execute(ip).get()

            if(ret == null){
                setStatusView(2)
            }else{
                setStatusView(1)
                MyIOSocket = ret as Socket
            }
        }else{
            setStatusView(3)
        }
        setLoading(false)
    }

    private fun setLoading(enabled: Boolean){
        if(enabled){
            progress_bar.visibility = View.VISIBLE //enable progress bar
            loading_view.visibility = View.VISIBLE //enable grey background
        }else{
            progress_bar.visibility = View.INVISIBLE //disable progress bar
            loading_view.visibility = View.INVISIBLE //disable grey background
        }
    }

    /**
     * function that sets the status view
     */
    @SuppressLint("SetTextI18n")
    private fun setStatusView(status: Int){
        when(status){
            0->{
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorLightGrey))
                //textView_status.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorLightGrey))
                textView_status.text = "connecting"
            }
            1->{
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorGreen))
                //textView_status.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorGreen))
                textView_status.text = "connected"
            }
            2->{
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
                //textView_status.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorRed))
                textView_status.text = "offline / not reachable"
            }
            3->{
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorLightGrey))
                textView_status.text = "no ip specified"
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
