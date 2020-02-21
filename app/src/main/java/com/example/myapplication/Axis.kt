package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_axis.view.*

class Axis : Fragment() {

    var distance: Double = 1.0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_axis, container, false)

        /**
         * test connection
         */
        val opts = IO.Options()
        opts.query = "token=Bearer $MyAuthToken"
        Log.e("test ", "" + opts.query)
        val socket: Socket = IO.socket("http://192.168.83.16:4000", opts)

        socket.connect()
            .on(Socket.EVENT_CONNECT) { Log.e("test ", "connected") }
            .on(Socket.EVENT_DISCONNECT) { Log.e("test ", "disconnected") }


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

            Toast.makeText(context, "moving $distance mm down ...", Toast.LENGTH_SHORT).show()
        }

        //upY
        view.btn_up.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

            socket?.emit("moveForward", distance.toString(), Ack{callback: Array<Any> ->
                callback.forEach {msg: Any ->
                    Log.e("test ", msg.toString())
                }
                Log.e("socketIOACK", it.toString())
            })
        }

        //left
        view.btn_left.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

        }

        //right
        view.btn_right.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

        }

        //homeXY
        view.btn_homeXY.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

        }

        //upZ
        view.btn_upZ.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

        }

        //downZ
        view.btn_downZ.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

        }

        //homeZ
        view.btn_homeZ.setOnClickListener{
            myVib?.vibrate(VibrationEffect.createOneShot(20, 1))

        }


        return view
    } //onViewCreate
}
