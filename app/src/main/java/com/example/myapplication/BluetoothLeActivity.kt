package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_bluetooth_le.*
import kotlinx.android.synthetic.main.activity_settings.toolbar
import kotlin.collections.ArrayList



private const val SCAN_PERIOD: Long = 10000

abstract class BluetoothLeActivity(var arrayAdapter: ArrayAdapter<String>) : AppCompatActivity() {

    //variables
    private val handler: Handler = Handler()
    private var currentlyScanning: Boolean = false

    var filters = ArrayList<ScanFilter>()

    var devices = ArrayList<BluetoothDevice>()
    var deviceNames = ArrayList<String>()


    /**
     * get bluetooth adapter
     */
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    @SuppressLint("ResourceType")
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
         * initializing scan filters
         */
        var scanFilter = ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("e081fec0-f757-4449-b9c9-bfa83133f7fc")).build()
        filters.add(scanFilter)


        /**
         * enable Bluetooth Intent if not already enabled
         *
         * !! = non null asserted call
         */
        if(!bluetoothAdapter!!.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        //testting
        deviceNames.add("Ble Device 1")
        deviceNames.add("Ble Device 2")
        deviceNames.add("Ble Device 3")
        deviceNames.add("Ble Device 4")
        //testing end
        arrayAdapter = ArrayAdapter<String>(this, R.id.listView_bluetoothLeDevices, R.id.textView_BleDeviceName, deviceNames)
        listView_bluetoothLeDevices.adapter = arrayAdapter


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
     * function:
     * scan for BLE devices
     */
    private fun scanLeDevices(enable: Boolean){

        val bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                currentlyScanning = false

                bluetoothLeScanner.stopScan(myLeScanCallback)
            }, SCAN_PERIOD)

            currentlyScanning = true
            bluetoothLeScanner.startScan(filters, ScanSettings.Builder().setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build(), myLeScanCallback)
        } else {
            currentlyScanning = false
            bluetoothLeScanner.stopScan(myLeScanCallback)
        }
    }


    //not working
    //might be removable
    /*
    private val myLeScanCallback = BluetoothAdapter.LeScanCallback{device, _, _ ->
        runOnUiThread{
            devices.add(device)
        }
    }*/


    /**
     * Callback from BLEScanner
     */
    private val myLeScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            devices.add(result.device)
            deviceNames.add(result.device.name)
            arrayAdapter.notifyDataSetChanged()
        }

        /*
        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }

         */
    }


}

