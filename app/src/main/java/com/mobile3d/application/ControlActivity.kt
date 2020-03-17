package com.mobile3d.application

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @myVib is used to make a physical haptic engine feedback on control button press
 */
var Engine: Vibrator? = null

var ControlSocket: Socket? = null

class ControlActivity : AppCompatActivity() {

    override fun onStop() {
        ControlSocket?.disconnect()

        super.onStop()
    }

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
         * Calls socketIOConnect function
         * -> sets up socketIO connection
         */
        CoroutineScope(Dispatchers.Main).launch {
            controlSocketConnect()

            ControlSocket?.on("log"){
                Snackbar.make( constraint_layout_control, it[0].toString(), Snackbar.LENGTH_SHORT).show()
            }
        }

        /**
         * initialize myVib with the system service Vibrator_Service
         */
        Engine = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }// onCreate

    private suspend fun controlSocketConnect() {
        val ip = SharedPref!!.getString("ip", "")

        val opts = IO.Options()
        opts.forceNew = true
        opts.timeout = 500
        opts.query = "token=Bearer $MyAuthToken"

        //Log.e("test ", opts.query)

        ControlSocket = IO.socket("http://$ip:4000", opts)

        ControlSocket!!.connect()
            .on(Socket.EVENT_CONNECT) {
                Log.e("test ", "connected control")

            }
            .on(Socket.EVENT_DISCONNECT) {
                Log.e("test ", "disconnected control")

                returnConnectionLost()
            }
            .on(Socket.EVENT_CONNECT_ERROR) {
                Log.e("test ", "connect_error control")

            }

    }   //socketIOConnect

    fun returnConnectionLost(){
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

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
