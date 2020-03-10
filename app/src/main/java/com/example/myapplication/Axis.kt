package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_axis.view.*

class Axis : Fragment() {

    var distance = 10

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_axis, container, false)

        /**
         * set movement length button listeners
         */
        view.btn_one_mm.setOnClickListener {
            distance = 1
            view.toggleGroup.check(R.id.btn_one_mm)
        }
        view.btn_ten_mm.setOnClickListener {
            distance = 10
            view.toggleGroup.check(R.id.btn_ten_mm)
        }
        view.btn_hundred_mm.setOnClickListener {
            distance = 100
            view.toggleGroup.check(R.id.btn_hundred_mm)
        }

        /**
         * set standard values from shared preference
         */
        when(SharedPref?.getString("standardStepSize","10mm")){
            "1mm" -> {
                view.toggleGroup.check(R.id.btn_one_mm)
            }
            "10mm" -> {
                view.toggleGroup.check(R.id.btn_ten_mm)
            }
            "100mm" -> {
                view.toggleGroup.check(R.id.btn_hundred_mm)
            }
        }

        /**
         * move button listeners
         * @see Engine.vibrate -> used to make physical vibration feedback on click
         */

        //downY
        view.btn_down.setOnClickListener {
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1)) //vibrate

            ControlSocket?.emit("moveBack", distance.toString())
        }

        //upY
        view.btn_up.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveForward", distance.toString())
        }

        //left
        view.btn_left.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveLeft", distance.toString())
        }

        //right
        view.btn_right.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveRight", distance.toString())
        }

        //homeXY
        view.btn_homeXY.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveXYHome", distance.toString())
        }

        //upZ
        view.btn_upZ.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveUp", distance.toString())
        }

        //downZ
        view.btn_downZ.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveDown", distance.toString())
        }

        //homeZ
        view.btn_homeZ.setOnClickListener{
            Engine?.vibrate(VibrationEffect.createOneShot(20, 1))

            ControlSocket?.emit("moveZHome", distance.toString())
        }


        return view
    } //onViewCreate
}
