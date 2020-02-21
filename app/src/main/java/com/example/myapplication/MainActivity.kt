package com.example.myapplication


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.android.material.snackbar.Snackbar
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * [maybe not necessary]
 * @GlobalAuthToken: global Variable with the auth bearer token needed for the socketIO connection / authentication
 *
 * @myIOSocket: SocketIO object that is used to communicate with the backend
 *
 * [maybe not necessary]
 * @BackendIP: String with the BackendIP (can be set manually or automatically by connecting the raspberry to a wifi via BLE)
 */
var MyAuthToken = ""
//var MyIOSocket: Socket? = null
var BackendIP: String = ""


/**
 * SharedPref object to access the local stored shared preferences data
 */
var SharedPref: SharedPreferences? = null

class MainActivity : AppCompatActivity() {

    private var socket: Socket? = null

    private val handler = Handler()

    override fun onRestart() {
        CoroutineScope(Main).launch {
            socketIOConnect()
        }

        /*finish()
        startActivity(intent)*/

        super.onRestart()
    }


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


        /**
         * TODO DOKU
         */
        CoroutineScope(Main).launch {
            socketIOConnect()
        }




        /**
         * listens to clicks of the Navigation Icon on the toolbar
         */
        toolbar.setNavigationOnClickListener {
            val drawerLayout = drawer_layout
            drawerLayout.openDrawer(GravityCompat.START)
        }

        btn_refresh.setOnClickListener {
            //snackbar test
            Snackbar.make(constraint_layout_main, "connection error - try to refresh", Snackbar.LENGTH_SHORT).show()

            /*CoroutineScope(Main).launch {
                socketIOConnect()
            }*/
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
     *
     * TODO removable
     */
    private fun getAuthToken() {
        val ip = SharedPref!!.getString("ip", "")

        if(ip!!.isNotEmpty()){

            val ret = GetAuthTokenAsyncTask().execute(ip).get()

            if (ret != null) {
                MyAuthToken = ret as String
            }
        }
    }

    /**
     *  TODO DOKU
     */
    private suspend fun socketIOConnect() {
        setLoading(true)
        var myStatus = 0

        delay(100)

            val ip = SharedPref!!.getString("ip", "")


            if (ip!!.isNotEmpty()) {

                if (MyAuthToken.isEmpty()) {

                    val ret = GetAuthTokenAsyncTask().execute(ip).get()

                    if (ret != null) {
                        MyAuthToken = ret as String
                    }
                }

                if (MyAuthToken.isNotEmpty()) {

                    val opts = IO.Options()
                    opts.forceNew = true
                    opts.timeout = 1800
                    opts.multiplex = false
                    opts.query = "token=Bearer $MyAuthToken"

                    Log.e("test ", opts.query)

                    socket = IO.socket("http://$ip:4000", opts)

                    socket!!.connect()
                        .on(Socket.EVENT_CONNECT) {
                            Log.e("test ", "connected")
                            myStatus = 1
//                            if (getStatus() != 1) {
//                                setStatusView(1)
//                            }
                        }
                        .on(Socket.EVENT_DISCONNECT) {
                            Log.e("test ", "disconnected")
                            myStatus = 2
//                            if (getStatus() != 2) {
//                                setStatusView(2)
//                            }
                            Snackbar.make(window.decorView.rootView, "connection error - try to refresh", Snackbar.LENGTH_SHORT).show()
                        }
                        .on(Socket.EVENT_CONNECT_ERROR) {
                            Log.e("test ", "connect_error")
                            myStatus = 2
                            /*if (getStatus() != 2) {
                                setStatusView(2)
                            }*/
                        }
                        .on(Socket.EVENT_CONNECT_TIMEOUT) {
                            Log.e("test ", "connect_timeout")
                            myStatus = 2
                            /*if (getStatus() != 2) {
                                setStatusView(2)
                            }*/
                        }

                    delay(2000)

                    when(myStatus){
                        1->{
                            Log.e("test ", "set connected")
                            setStatusView(1)
                        }
                        2->{
                            Log.e("test ", "set disconnected")
                            setStatusView(2)
                        }
                    }

                } else {    //MyAuthToken is empty
                    setStatusView(2)
                }
            } else {    //ip from shared Preference is empty -> no ip specified
                setStatusView(3)
            }

            setLoading(false)

    }   //socketIOConnect

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
    @SuppressLint("SetTextI18n")    //suppress string value warning
    private fun setStatusView(status: Int){
        when(status){
            0->{
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorLightGrey))
                textView_status.setText("connecting")
                textView_current_action.text = "-"
                textView_ip.text = ""
            }
            1->{
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorGreen))
                setLoading(false)
                textView_status.setText("connected")
                textView_current_action.text = "todo"
                textView_ip.text = "${SharedPref!!.getString("ip", "")}"
            }
            2->{
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
                setLoading(false)
                textView_status.setText("offline / not reachable")
                textView_current_action.text = "-"
                textView_ip.text = "${SharedPref!!.getString("ip", "")}"
            }
            3->{
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorLightGrey))
                setLoading(false)
                textView_status.setText("no ip specified")
                textView_current_action.text = "-"
                textView_ip.text = ""
            }
        }
    }

    private fun getStatus(): Int {
        when (textView_status.text) {
            "connecting" -> {
                return 0
            }
            "connected" -> {
                return 1
            }
            "offline / not reachable" -> {
                return 2
            }
            "no ip specified" -> {
                return 3
            }
        }
        return -1
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
