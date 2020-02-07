package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_socket_setup.*

class SocketSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket_setup)


        //init toolbar
        var toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Socket Config"

        /**
         * listens to clicks of the Navigation Icon
         */
        toolbar.setNavigationOnClickListener {
            finish()
        }

        /**
         * on btn save changes click listener
         *
         * checks if ip input format is valid, then calls ConnectSocketAsyncTask
         */
        btn_save_changes.setOnClickListener{
            val ip = text_input_ip.text.toString()
            if(Patterns.IP_ADDRESS.matcher(ip).matches()){
                text_input_ip.error = null
                BackendIP = ip

                var ret = ConnectSocketAsyncTask().execute(ip).get()
                Log.e("test ", " $ret")
                if(ret != null){
                    myIOSocket = ret as Socket

                    displayConnectionMsg(true)

                    //all successfully done return to main activity
                    finish()
                }else{
                    displayConnectionMsg(false)
                }

            }else{
                text_input_ip.error = "wrong ip format"
            }
        }

    } //onCreate

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
