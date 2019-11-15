package com.example.myapplication

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*
import androidx.core.os.HandlerCompat.postDelayed
import java.lang.Compiler.enable
import android.bluetooth.le.BluetoothLeScanner
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




private const val SCAN_PERIOD: Long = 10000

class BluetoothLeActivity : AppCompatActivity() {

    //variables
    private val handler: Handler = Handler()
    private var currentlyScanning: Boolean = false
    var filters = ArrayList<String>()


    /**
     * Bluetooth LE Setup
     */
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_le)

        //toolbar
        var toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Wifi Setup"

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        //_toolbar

        /**
         * enable Bluetooth Intent if not already enabled
         * !! = non null asserted call
         */
        if(!bluetoothAdapter!!.isEnabled){
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
            //super.onActivityResult(requestCode, resultCode, data)
        }
    }



    /**
     * scan for BLE devices
     * TODO implement LeScanCallback
     */
    private fun scanLeDevicec(enable: Boolean){

        val bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                currentlyScanning = false

                //bluetoothLeScanner.stopScan(mLeScanCallback)
            }, SCAN_PERIOD)

            currentlyScanning = true
            //bluetoothLeScanner.startScan(mLeScanCallback)
        } else {
            currentlyScanning = false
            //bluetoothLeScanner.stopScan(mLeScanCallback)
        }


    }

}

