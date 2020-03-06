package com.example.myapplication


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

const val SOCKET_IO_CONNECTION_REQUEST = 140

/**
 * [maybe not necessary]
 * @GlobalAuthToken: global Variable with the auth bearer token needed for the socketIO connection / authentication
 *
 * @myIOSocket: SocketIO object that is used to communicate with the backend
 *
 * TODO [maybe not necessary]
 * @BackendIP: String with the BackendIP (can be set manually or automatically by connecting the raspberry to a wifi via BLE)
 */
var MyAuthToken = ""
var BackendIP: String = ""


/**
 * SharedPref object to access the local stored shared preferences data
 */
var SharedPref: SharedPreferences? = null

class MainActivity : AppCompatActivity() {

    private var refreshEnableFlag = false

    private var socket: Socket? = null

    /**
     * on restart of the activity
     * if the ip changed
     *  - disconnect from the old socket io connection
     *  - and try to connect to the backend with the new ip
     */
    override fun onRestart() {
        if(BackendIP != SharedPref!!.getString("ip", "impossibleBackendIPValue")) {
            socket?.disconnect()
            CoroutineScope(Main).launch {
                socketIOConnect()
            }
        }

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
        SharedPref = getSharedPreferences("mobile3d.preferences", MODE_PRIVATE)


        /**
         * Calls socketIOConnect function
         * -> sets up socketIO connection
         */
        CoroutineScope(Dispatchers.IO).launch {
            socketIOConnect()
        }

        /**
         * if refreshEnableFlag ist true it emits a getInfo event every 2 seconds
         */
        CoroutineScope(Dispatchers.Default).launch {
            while (true){
                if(refreshEnableFlag) {
                    socket?.emit("getInfo")
                }
                delay(5000)
            }
        }



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

                    if (getStatus() == 1) {
                        //open control activity
                        val intent = Intent(this, ControlActivity::class.java)
                        startActivityForResult(intent, SOCKET_IO_CONNECTION_REQUEST)
                    } else {
                        MaterialAlertDialogBuilder(this)
                            .setTitle("No connection")
                            .setMessage("in order to access the controls you have to be connected to a printer")
                            .setPositiveButton("OK") { _, _ ->
                                //do nothing on OK
                            }.create().show()
                    }

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
     *
     *
     *  TODO DOKU
     */
    private suspend fun socketIOConnect() {
        setLoading(true)

        delay(500)

            val ip = SharedPref!!.getString("ip", "")


            if (ip!!.isNotEmpty()) {

                BackendIP = ip

                if (MyAuthToken.isEmpty()) {

                    val ret = GetAuthTokenAsyncTask().execute(ip).get()

                    MyAuthToken = if (ret != null) {
                        ret as String
                    }else{
                        SharedPref!!.getString("auth", "")!!
                    }
                }

                if (MyAuthToken.isNotEmpty()) {

                    val opts = IO.Options()
                    opts.forceNew = true
                    opts.timeout = 500
                    opts.query = "token=Bearer $MyAuthToken"

                    //Log.e("test ", opts.query)

                    socket = IO.socket("http://$ip:4000", opts)

                    socket!!.connect()
                        .on(Socket.EVENT_CONNECT) {
                            Log.e("test ", "connected")
                            setStatusView(1)

                            refreshEnableFlag = true
                        }
                        .on(Socket.EVENT_DISCONNECT) {
                            Log.e("test ", "disconnected")
                            setStatusView(2)
                            displayInfo("")

                            refreshEnableFlag = false

                            Snackbar.make(constraint_layout_main, "connection error - disconnected", Snackbar.LENGTH_SHORT).show()
                        }
                        .on(Socket.EVENT_CONNECT_ERROR) {
                            Log.e("test ", "connect_error")
                            setStatusView(2)
                            displayInfo("")
                        }
                        .on("info"){
                            displayInfo(it[0].toString())
                            Log.e("test ", "info received")
                        }
                    /*.on(Socket.EVENT_CONNECT_TIMEOUT) {
                        Log.e("test ", "connect_timeout")
                        myStatus = 2
                        if (getStatus() != 2) {
                            setStatusView(2)
                        }
                    }*/

                } else {    //MyAuthToken is empty
                    setStatusView(2)
                }
            } else {    //ip from shared Preference is empty -> no ip specified
                setStatusView(3)
            }

    }   //socketIOConnect

    @SuppressLint("SetTextI18n")
    private fun displayInfo(info: String){
        runOnUiThread {
            if(info == ""){
                textView_current_action.text = "-"
                progress_bar_print.progress = 0
                textView_print_progress_percent.text = "-"
                textView_hotend_temperature_current.text = "-"
                textView_hotend_temperature_set.text = ""
                textView_heatbed_temperature_current.text = "-"
                textView_heatbed_temperature_set.text = ""
            }else {
                val myInfo = JSONObject(info)
                textView_current_action.text = myInfo.getString("status")

                val progress = myInfo.getJSONObject("progress")
                val sent = progress.getDouble("sent")
                val total = progress.getDouble("total")
                val percent = ((sent / total) * 100).toInt()
                progress_bar_print.progress = percent
                textView_print_progress_percent.text = "$percent%"

                val temperature = myInfo.getJSONObject("temperature")
                val hotend = temperature.getJSONObject("hotend")
                val heatbed = temperature.getJSONObject("heatbed")
                textView_hotend_temperature_current.text = hotend.getString("current") + "°C"
                textView_hotend_temperature_set.text = " / " + hotend.getString("set") + "°C"
                textView_heatbed_temperature_current.text = heatbed.getString("current") + "°C"
                textView_heatbed_temperature_set.text = " / " + heatbed.getString("set") + "°C"
            }
        }
    }

    /**
     * enables / disables loading animation
     */
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
        runOnUiThread {
            when (status) {
                0 -> {
                    textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorLightGrey))
                    textView_status.setText("connecting")
                    textView_ip.text = ""
                }
                1 -> {
                    textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorGreen))
                    setLoading(false)
                    textView_status.setText("connected")
                    textView_ip.text = "${SharedPref!!.getString("ip", "")}"
                }
                2 -> {
                    textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
                    setLoading(false)
                    textView_status.setText("offline / not reachable")
                    textView_ip.text = "${SharedPref!!.getString("ip", "")}"
                }
                3 -> {
                    textView_status.setTextColor(ContextCompat.getColor(this, R.color.colorLightGrey))
                    setLoading(false)
                    textView_status.setText("no ip specified")
                    textView_ip.text = ""
                }
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
        return -1   //shouldn't get to this point
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == SOCKET_IO_CONNECTION_REQUEST && resultCode == Activity.RESULT_OK){
            MaterialAlertDialogBuilder(this)
                .setTitle("connection error ")
                .setMessage("disconnected - connection lost")
                .setPositiveButton("OK") { _, _ ->
                    //do nothing on ok
                }.create().show()
        }

        super.onActivityResult(requestCode, resultCode, data)
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
