package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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

        val handler = Handler()

        /**
         * set movement length button listeners
         */
        btn_one_mm.setOnClickListener {
            toggleGroup.check(R.id.btn_one_mm)
        }
        btn_ten_mm.setOnClickListener {
            toggleGroup.check(R.id.btn_ten_mm)
        }
        btn_hundred_mm.setOnClickListener {
            toggleGroup.check(R.id.btn_hundred_mm)
        }

        /**
         * set standard values from shared preference
         */
        when(SharedPref?.getString("standardStepSize","10mm")){
            "1mm" -> {
                toggleGroup.check(R.id.btn_one_mm)
            }
            "10mm" -> {
                toggleGroup.check(R.id.btn_ten_mm)
            }
            "100mm" -> {
                toggleGroup.check(R.id.btn_hundred_mm)
            }
        }
        text_input_distance.setText(SharedPref?.getString("standardDistance", "5"))
        text_input_nozzle_temperature.setText(SharedPref?.getString("standardNozzleTemp", "200"))
        text_input_bed_temperature.setText(SharedPref?.getString("standardBedTemp", "60"))
        text_input_fan_speed.setText(SharedPref?.getString("standardFanSpeed", "100"))



        val editor = SharedPref?.edit()
        /**
         * saves values to shared preference
         */
        btn_save_changes.setOnClickListener {
            var stepSize = ""
            when (toggleGroup.checkedButtonId){
                R.id.btn_one_mm -> {
                    stepSize = "1mm"
                }
                R.id.btn_ten_mm -> {
                    stepSize = "10mm"
                }
                R.id.btn_hundred_mm -> {
                    stepSize = "100mm"
                }
            }
            editor?.putString("standardStepSize", stepSize)

            var error = 0

            var distance: Int = text_input_distance.text.toString().toInt()
            if(distance < 0 || distance > 100){
                text_input_distance.error = "must be between 0 and 100"
                error = 1
            }else{
                editor?.putString("standardDistance", distance.toString())
            }

            var nozzleTemp = text_input_nozzle_temperature.text.toString().toInt()
            if(nozzleTemp < 0 || nozzleTemp > 300){
                text_input_nozzle_temperature.error = "must be between 0 and 300"
                error = 1
            }else{
                editor?.putString("standardNozzleTemp", nozzleTemp.toString())
            }

            var bedTemp = text_input_bed_temperature.text.toString().toInt()
            if(bedTemp < 0 ||bedTemp > 200){
                text_input_bed_temperature.error = "must be between 0 and 200"
                error = 1
            }else{
                editor?.putString("standardBedTemp", bedTemp.toString())
            }

            var fanSpeed = text_input_fan_speed.text.toString().toInt()
            if(fanSpeed < 0 || fanSpeed > 100){
                text_input_fan_speed.error = "must be between 0 and 100"
                error = 1
            }else{
                editor?.putString("standardFanSpeed", fanSpeed.toString())
            }

            if(error == 0){
                editor?.apply()
                Snackbar.make(it, "saved changes", Snackbar.LENGTH_SHORT).show()

                Handler().postDelayed({
                    finish()
                },1000)

            }


        } //btnSaveChanges


    } //OnCreate


    /**
     * initializes sidebar menu layout
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

}
