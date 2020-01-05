package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        var toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Settings"

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })


        var notificationSwitch = switch_notifications

        notificationSwitch.setOnClickListener {
            if(notificationSwitch.isChecked){
                Toast.makeText(this, "Notifications ON", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Notifications OFF", Toast.LENGTH_SHORT).show()
            }
        }
    }




}
