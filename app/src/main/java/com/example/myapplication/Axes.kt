package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_axes.*
import kotlinx.android.synthetic.main.fragment_axes.view.*

class Axes : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_axes, container, false)

        view.btn_down.setOnClickListener {
            Toast.makeText(activity, "moving down ...", Toast.LENGTH_SHORT).show()
        }

        return view
    }


}
