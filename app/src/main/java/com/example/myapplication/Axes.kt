package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_axis.view.*

class Axes : Fragment() {

    var distance: Double = 1.0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_axis, container, false)

        /**
         * test connection
         */
        val opts = IO.Options()
        opts.query = "token=Bearer $GlobalAuthToken"
        Log.e("test ", "" + opts.query)
        val socket: Socket = IO.socket("http://192.168.83.16:4000", opts)

        socket.connect()
            .on(Socket.EVENT_CONNECT) { Log.e("test ", "connected") }
            .on(Socket.EVENT_DISCONNECT) { Log.e("test ", "disconnected") }




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

        view.btn_up.setOnClickListener{
            socket.emit("moveForward", distance.toString(), Ack{
                it.forEach {msg: Any ->
                    Log.e("test ", msg.toString())
                }
                //Log.e("test ", it.toString())
            })
        }

        view.btn_left.setOnClickListener{

        }

        return view
    } //onViewCreate
}
