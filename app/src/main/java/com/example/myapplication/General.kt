package com.example.myapplication


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_general.view.*
import java.util.zip.Inflater


class General : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_general, container, false)

        //checks if the text input fields have focus and if they do not have focus it hides the keyboard
        view.text_input_fan_speed.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus){
                hideKeyboard(view)
            }
        }

        return view
    }

    //function that hides the keyboard
    fun hideKeyboard(view: View){
        var inputMethodManager: InputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
