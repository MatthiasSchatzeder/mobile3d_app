package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_socket_setup.*

class SocketSetupActivity : AppCompatActivity() {

    val handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket_setup)


        //init toolbar
        val toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Socket Config"

        /**
         * listens to clicks of the Navigation Icon
         */
        toolbar.setNavigationOnClickListener {
            finish()
        }

        /**
         * create a sharedPref object of the "mobile3d.preferences_ip" file
         */
        //val sharedPref = getSharedPreferences("mobile3d.preferences_ip", MODE_PRIVATE)
        val editor = SharedPref!!.edit()

        /**
         * sets text input field to the stored ip of the sharedPref / of no ip is stored it is set to an empty string ("")
         */
        text_input_ip.setText(SharedPref!!.getString("ip", ""))

        /**
         * on btn save changes click listener
         *
         * checks if ip input format is valid, then calls ConnectSocketAsyncTask
         */
        btn_save_changes.setOnClickListener{
            setLoading(true)
            handler.postDelayed({
                val ip = text_input_ip.text.toString()

                if(Patterns.IP_ADDRESS.matcher(ip).matches()){

                    text_input_ip.error = null
                    BackendIP = ip

                    val ret = ConnectSocketAsyncTask().execute(ip).get()
                    //Log.e("test ", " $ret")

                    if(ret != null){
                        MyIOSocket = ret as Socket

                        displayConnectionMsg(true)

                        //all successfully done return to main activity
                        finish()
                    }else{
                        displayConnectionMsg(false)
                    }

                    /**
                     * writes ip to the sharedPreference
                     */
                    editor.putString("ip", ip)
                    editor.apply()

                }else{
                    text_input_ip.error = "wrong ip format"
                }
                setLoading(false)
            }, 10)

        }   //btn listener

    } //onCreate

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
     * toolbar menu init
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    /**
     * displays an connection status toast
     * TODO: toast looks very ugly -> fix
     */
    private fun displayConnectionMsg(connected: Boolean){
        if(connected){
            val toast = Toast.makeText(this, "connected", Toast.LENGTH_LONG)
            val view = toast.view

            view.setBackgroundResource(R.color.colorGreen)
            toast.show()
        }else{
            val toast = Toast.makeText(this, "ip not reachable", Toast.LENGTH_LONG)
            val view = toast.view

            view.setBackgroundResource(R.color.colorRed)
            toast.show()
        }
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
