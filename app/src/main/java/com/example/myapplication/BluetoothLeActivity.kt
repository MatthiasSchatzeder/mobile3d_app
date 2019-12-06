package com.example.myapplication

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bluetooth_le.*
import kotlinx.android.synthetic.main.activity_settings.toolbar


private const val SCAN_PERIOD: Long = 10000
private const val ENABLE_BT_REQUEST_CODE = 1
private const val MY_REQUEST_FINE_LOCATION_PERMISSION = 10
private const val MY_REQUEST_BACKGROUND_LOCATION_PERMISSION = 11
private const val PERMISSION_REQUEST_FINE_LOCATION = 21
private const val PERMISSION_REQUEST_BACKGROUND_LOCATION = 22


class BluetoothLeActivity: AppCompatActivity() {


    //variables
    private val handler: Handler = Handler()
    private var currentlyScanning: Boolean = false

    var filters = ArrayList<ScanFilter>()

    var devices = ArrayList<BluetoothDevice>()
    var deviceNames = ArrayList<String>()



    /**
     * declare BT adapter
     */
    private var bluetoothAdapter: BluetoothAdapter? = null //BluetoothAdapter.getDefaultAdapter()


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
         * get BT manager and BT adapter
         */
        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter



        /*
        //testting
        deviceNames.add("Ble Device 1")
        deviceNames.add("Ble Device 2")
        deviceNames.add("Ble Device 3")
        deviceNames.add("Ble Device 4")
        //testing end
         */

        /**
         * link ListView and array adapter
         */
        var arrayAdapter = ArrayAdapter<String>(this, R.layout.bluetooth_le_device_list_item, deviceNames)
        listView_bluetoothLeDevices.adapter = arrayAdapter

        /**
         * btn to start scan method
         */
        btn_scanDevices.setOnClickListener{
            if(currentlyScanning){
                Toast.makeText(this, "already scanning ...", Toast.LENGTH_SHORT).show()
            }else{
                scanLeDevices(true)
            }

        }

        /**
         * function call to check for BT
         */
        checkBT()

        /**
         * check permission function call
         */
        checkPermission()

    } //OnCreate

    /**
     * enable Bluetooth Intent if not already enabled
     */
    private fun checkBT(){
        if(bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_CODE)
        }
    }



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

        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                currentlyScanning = false

                bluetoothLeScanner?.stopScan(myLeScanCallback)
            }, SCAN_PERIOD)

            currentlyScanning = true
            //bluetoothLeScanner.startScan(filters, ScanSettings.Builder().setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build(), myLeScanCallback)
            bluetoothLeScanner?.startScan(myLeScanCallback)
        } else {
            currentlyScanning = false
            bluetoothLeScanner?.stopScan(myLeScanCallback)
        }
    }


    /**
     * Callback from BLEScanner
     */
    private val myLeScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            devices.add(result.device)
            deviceNames.add(result.device.name)
            Toast.makeText(this@BluetoothLeActivity, "Device found", Toast.LENGTH_SHORT).show()
            //arrayAdapter.notifyDataSetChanged()
        }
        /*
        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
        }


        override fun onScanFailed(errorCode: Int) {
            Toast.makeText(this@BluetoothLeActivity, "Fail", Toast.LENGTH_SHORT).show()
            super.onScanFailed(errorCode)
        }

         */
    }

    /**
     * Android Q check (and enable if not already done) location permission needed for BLE
     * source: https://stackoverflow.com/questions/32708374/bluetooth-le-scan-doesnt-work-on-android-m-in-the-background
     */
    @TargetApi(23)
    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder.setTitle("This app needs background location access")
                        builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener{
                            requestPermissions(
                                Array(5){ Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                MY_REQUEST_BACKGROUND_LOCATION_PERMISSION)
                        }
                        builder.show()
                    }
                    else {
                        val  builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder.setTitle("Functionality limited")
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener{
                            finish()
                        }
                        builder.show()
                    }

                }
            } else {
                if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(Array(5){ Manifest.permission.ACCESS_FINE_LOCATION; Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        MY_REQUEST_FINE_LOCATION_PERMISSION)
                }
                else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener {
                        finish()
                    }
                    builder.show()
                }
            }
        }
    } //fun


} //class

