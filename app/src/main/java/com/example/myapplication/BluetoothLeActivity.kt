package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*

//extension property
private val BluetoothAdapter.isDisabled: Boolean
    @SuppressLint("MissingPermission")
    get() = !isEnabled      //permission is added, although an error is displayed

class BluetoothLeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_le)

        var toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Wifi Setup"

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        /**
         * Bluetooth LE Setup
         */
        val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        }

        // !! = non null asserted call
        if(bluetoothAdapter!!.isDisabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }









    }

    /**
     * gets result from BluetoothAdapter.ACTION_REQUEST_ENABLE
     * if the user declined BT enable the activity calls finish()
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_CANCELED){
                finish()
            }
            else{
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}

