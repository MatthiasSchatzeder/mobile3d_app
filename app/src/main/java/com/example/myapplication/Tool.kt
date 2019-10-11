package com.example.myapplication


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_tool.*
import kotlinx.android.synthetic.main.fragment_tool.view.*
import kotlinx.android.synthetic.main.fragment_tool.view.text_input_distance
import kotlin.math.absoluteValue
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService




class Tool() : Fragment() {

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


        return view
    }

    //function that hides the keyboard
    fun hideKeyboard(view: View){
        val inputMethodManager: InputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
