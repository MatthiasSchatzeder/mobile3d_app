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

        var distance: Double? = 1.0
        view.btn_zero_point_one_mm.setOnClickListener {
            distance = 0.1
            view.toggleGroup.check(R.id.btn_zero_point_one_mm)
        }
        view.btn_one_mm.setOnClickListener {
            distance = 1.0
            view.toggleGroup.check(R.id.btn_one_mm)
        }
        view.btn_ten_mm.setOnClickListener {
            distance = 10.0
            view.toggleGroup.check(R.id.btn_ten_mm)
        }

        view.btn_down.setOnClickListener {
            Toast.makeText(context, "moving $distance mm down ...", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
