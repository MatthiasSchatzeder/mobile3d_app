package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_axis.view.*

class Axis : Fragment() {

    var distance: Double = 1.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_axis, container, false)

        /**
         * set movement length button listeners
         */
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


        /**
         * move button listeners
         * @see myVib.vibrate -> used to make physical vibration feedback on click
         */

        //downY
        view.btn_down.setOnClickListener {
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1)) //vibrate

            ControlSocket?.emit("moveBack", distance.toString())
        }

        //upY
        view.btn_up.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveForward", distance.toString())
        }

        //left
        view.btn_left.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveLeft", distance.toString())
        }

        //right
        view.btn_right.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveRight", distance.toString())
        }

        //homeXY
        view.btn_homeXY.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveXYHome", distance.toString())
        }

        //upZ
        view.btn_upZ.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveUp", distance.toString())
        }

        //downZ
        view.btn_downZ.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveDown", distance.toString())
        }

        //homeZ
        view.btn_homeZ.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveZHome", distance.toString())
        }


        return view
    } //onViewCreate
}
