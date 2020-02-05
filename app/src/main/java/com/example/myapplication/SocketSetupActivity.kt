package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
         */
        btn_save_changes.setOnClickListener{
            val ip = text_input_ip.text.toString()
            if(Patterns.IP_ADDRESS.matcher(ip).matches()){
                text_input_ip.error = null
                BackendIP = ip
                finish()
            }else{
                text_input_ip.error = "wrong ip format"
            }
        }

    } //onCreate

    /**
     * function that checks if the ip address is valid
     */
    private fun validateIP(ip: String): Boolean{
        return Patterns.IP_ADDRESS.matcher(ip).matches()
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
