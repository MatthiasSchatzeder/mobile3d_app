package com.example.myapplication


import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_general.*
import kotlinx.android.synthetic.main.fragment_general.view.*


class General : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_general, container, false)

        //checks if the text input fields have focus and if they do not have focus it hides the keyboard
        view.text_input_fan_speed.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                hideKeyboard(view)
            }
        }

        /**
         * set standard values from shared preference
         */
        view.text_input_fan_speed.setText(SharedPref?.getString("standardFanSpeed", "100"))

        /**
         * action button on click listeners
         */

        //set fan speed
        view.btn_set_fan_speed.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            val fanSpeed = view.text_input_fan_speed.text.toString().toInt()
            if(fanSpeed < 0 ||fanSpeed > 100){
                view.text_input_fan_speed.error = "must be between 0 and 100"
            }else{
                ControlSocket?.emit("fanOn", text_input_fan_speed.text.toString())
            }
        }

        //fan off
        view.btn_fan_off.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("fanOff")
        }

        return view
    }

    //function that hides the keyboard
    private fun hideKeyboard(view: View){
        val inputMethodManager: InputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
