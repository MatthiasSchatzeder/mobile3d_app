package com.example.myapplication


import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_tool.*
import kotlinx.android.synthetic.main.fragment_tool.view.*


class Tool : Fragment() {

    //TODO change min APi to 26 to fix deprecated error
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tool, container, false)

        view.btn_extrude.setOnClickListener {
            val distance: Int = text_input_distance.text.toString().toInt()

            Toast.makeText(context, "extruding " + distance + "mm", Toast.LENGTH_SHORT).show()
        }

        /**
         * checks if the text input fields have focus and if they do not have focus it hides the keyboard
         */
        view.text_input_distance.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                hideKeyboard(view)
            }
        }

        view.text_input_nozzle_temperature.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                hideKeyboard(view)
            }
        }

        view.text_input_bed_temperature.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                hideKeyboard(view)
            }
        }

        /**
         * set standard values from shared preference
         */
        view.text_input_distance.setText(SharedPref?.getString("standardDistance", "5"))
        view.text_input_nozzle_temperature.setText(SharedPref?.getString("standardNozzleTemp", "200"))
        view.text_input_bed_temperature.setText(SharedPref?.getString("standardBedTemp", "60"))

        /**
         * action buttons on click listeners
         */

        //retract
        view.btn_retract.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            val distance = view.text_input_distance.text.toString().toInt()
            if(distance < 0 || distance > 100){
                view.text_input_distance.error = "must be between 0 and 100"
            }else{
                ControlSocket?.emit("retract", text_input_distance.text.toString())
            }
        }

        //extrude
        view.btn_extrude.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            val distance = view.text_input_distance.text.toString().toInt()
            if(distance < 0 || distance > 100){
                view.text_input_distance.error = "must be between 0 and 100"
            }else{
                ControlSocket?.emit("extrude", text_input_distance.text.toString())
            }
        }

        //set nozzle temperature
        view.btn_set_nozzle_temperature.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            val nozzleTemp = view.text_input_nozzle_temperature.text.toString().toInt()
            if(nozzleTemp < 0 || nozzleTemp > 300){
                view.text_input_nozzle_temperature.error = "must be between 0 and 300"
            }else{
                ControlSocket?.emit("setHotendTemperature", text_input_nozzle_temperature.text.toString())
            }
        }

        //cool down nozzle
        view.btn_cool_down_nozzle.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("setHotendTemperature", "0")
        }

        //set bed temperature
        view.btn_set_bed_temperature.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            val bedTemp = view.text_input_bed_temperature.text.toString().toInt()
            if(bedTemp < 0 || bedTemp > 200){
                view.text_input_bed_temperature.error = "must be between 0 and 200"
            }else{
                ControlSocket?.emit("setHeatbedTemperature", text_input_bed_temperature.text.toString())
            }
        }

        //cool down bed
        view.btn_cool_down_bed.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("setHeatbedTemperature", "0")
        }


        return view
    }

    /**
     * function that hides the keyboard
     */
    private fun hideKeyboard(view: View){
        val inputMethodManager: InputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
