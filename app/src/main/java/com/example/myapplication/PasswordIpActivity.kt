package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import kotlinx.android.synthetic.main.activity_password_ip.*

class PasswordIpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_ip)

        /**
         * toolbar init
         */
        val toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "WifiSetup"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        /**
         * on connect click listener
         */
        btn_connect.setOnClickListener{
            val password = textInput_password.text.toString()
            val ssid = textInput_ssid.text.toString()
            var error = 0
            var returnIntent = Intent()

            if(password.isEmpty()){
                textInput_password.error = "must not be empty"
                error = 1
            }else{
                returnIntent.putExtra("password", password)
            }

            if(ssid.isEmpty()){
                textInput_ssid.error = "must not be empty"
                error = 1
            }else{
                returnIntent.putExtra("ssid", ssid)
            }

            if(error == 0){
                // finish and return password and ssid with intent extras
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }

        }

    } //onCreate

    /**
     * initializes sidebar menu layout
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}
