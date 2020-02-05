package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        /**
         * toolbar init
         */
        val toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Settings"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        //val notificationSwitch = switch_notifications

        /**
         * notification switch to turn on notifications
         * TODO background notifications
         */
        switch_notifications.setOnClickListener {
            if(switch_notifications.isChecked){
                Toast.makeText(this, "Notifications ON", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Notifications OFF", Toast.LENGTH_SHORT).show()
            }
        }
    }




}
