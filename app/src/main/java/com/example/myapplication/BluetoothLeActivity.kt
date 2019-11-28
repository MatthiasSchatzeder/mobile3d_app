package com.example.myapplication

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_bluetooth_le.*
import kotlinx.android.synthetic.main.activity_settings.toolbar
import android.Manifest
import android.bluetooth.le.ScanSettings
import android.util.Log
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.util.logging.LogManager


private const val SCAN_PERIOD: Long = 10000
private const val ENABLE_BT_REQUEST_CODE = 1
private const val MY_REQUEST_PERMISSON_CODE = 111

class BluetoothLeActivity: AppCompatActivity() {


    //variables
    private val handler: Handler = Handler()
    private var currentlyScanning: Boolean = false

    var filters = ArrayList<ScanFilter>()

    var devices = ArrayList<BluetoothDevice>()
    var deviceNames = ArrayList<String>()




    /**
     * get BLE Adapter
     */
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()!!


    /**
     * OnCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_le)

        /**
         * toolbar setup
         */
        var toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Wifi Setup"

        toolbar.setNavigationOnClickListener{
            finish()
        }

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
        if(!bluetoothAdapter.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_CODE)
        }

        checkPermission()

        /*
        //testting
        deviceNames.add("Ble Device 1")
        deviceNames.add("Ble Device 2")
        deviceNames.add("Ble Device 3")
        deviceNames.add("Ble Device 4")
        //testing end
         */

        var arrayAdapter = ArrayAdapter<String>(this, R.layout.bluetooth_le_device_list_item, deviceNames)
        listView_bluetoothLeDevices.adapter = arrayAdapter

        /**
         * error causes app crashing
         * reason: no location permission
         */
        //scanLeDevices(true)

    } //OnCreate

    /**
     * gets result from BluetoothAdapter.ACTION_REQUEST_ENABLE
     * if the user declined BT enable the activity calls finish()
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == ENABLE_BT_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED){
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)

    }


    /**
     * function:
     * scan for BLE devices
     */
    private fun scanLeDevices(enable: Boolean){

        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                currentlyScanning = false

                bluetoothLeScanner.stopScan(myLeScanCallback)
            }, SCAN_PERIOD)

            currentlyScanning = true
            //bluetoothLeScanner.startScan(filters, ScanSettings.Builder().setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build(), myLeScanCallback)
            bluetoothLeScanner.startScan(myLeScanCallback)
        } else {
            currentlyScanning = false
            bluetoothLeScanner.stopScan(myLeScanCallback)
        }
    }


    /**
     * Callback from BLEScanner
     */
    private val myLeScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            devices.add(result.device)
            deviceNames.add(result.device.name)
            //arrayAdapter.notifyDataSetChanged()
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


    private fun checkPermission(){
        val hasForegroundLocationPermission = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasForegroundLocationPermission) {
            val hasBackgroundLocationPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (hasBackgroundLocationPermission) {
                // handle location update
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), MY_REQUEST_PERMISSON_CODE)
            }
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION), MY_REQUEST_PERMISSON_CODE)
        }
    }


}

