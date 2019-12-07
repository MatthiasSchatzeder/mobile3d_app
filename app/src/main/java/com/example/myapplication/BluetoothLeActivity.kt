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
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_bluetooth_le.*
import kotlinx.android.synthetic.main.activity_settings.toolbar


private const val SCAN_PERIOD: Long = 10000
private const val ENABLE_BT_REQUEST_CODE = 1
private const val MY_REQUEST_FINE_LOCATION_PERMISSION = 10
private const val MY_REQUEST_BACKGROUND_LOCATION_PERMISSION = 11
private const val PERMISSION_REQUEST_FINE_LOCATION = 21
private const val PERMISSION_REQUEST_BACKGROUND_LOCATION = 22

private const val REQUEST_CODE_FOREGROUND = 30
private const val REQUEST_CODE_BACKGROUND = 31
private const val REQUEST_CODE_BLE_LOCATION = 32

class BluetoothLeActivity: AppCompatActivity() {


    //variables
    private val handler: Handler = Handler()
    private var currentlyScanning: Boolean = false

    var filters = ArrayList<ScanFilter>()

    var devices = ArrayList<BluetoothDevice>()
    var deviceNames = ArrayList<String>()

    var arrayAdapter: ArrayAdapter<String>? = null

    override fun onStop() {
        if(currentlyScanning){
            scanLeDevices(false)
        }

        super.onStop()
    }


    /**
     * declare BT adapter
     */
    private var bluetoothAdapter: BluetoothAdapter? = null

    /**
     * declare location Manager
     */
    private var locationManager: LocationManager? = null


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
        supportActionBar?.subtitle = "Scan and Select your Device"

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

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
        arrayAdapter = ArrayAdapter<String>(this, R.layout.bluetooth_le_device_list_item, deviceNames)
        listView_bluetoothLeDevices.adapter = arrayAdapter

        /**
         * btn to start scan method
         */
        btn_scanDevices.setOnClickListener{
            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
                if (currentlyScanning) {
                    Toast.makeText(this, "already scanning ...", Toast.LENGTH_SHORT).show()
                } else {
                    scanLeDevices(true)
                }
            }else{
                //Toast.makeText(this, "Scanning does not work without GPS enabled", Toast.LENGTH_LONG).show()
                /**
                 * enable GPS
                 */
                checkGPS()
            }


        } //btn listener

        /**
         * check permission function call
         */
        checkPermission()

        /**
         * function call to check for BT
         */
        checkBT()



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

    private fun checkGPS(){
        if (!locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("GPS is disabled!\nScanning for devices does not work without location tracking enabled")
        .setCancelable(false)
        .setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
            var intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
        }
        val alert: AlertDialog= builder.create()
        alert.show()
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
            if(result.device != null && result.device.name != null){
                if(!devices.contains(result.device)){
                    devices.add(result.device)
                    deviceNames.add(result.device.name)
                    arrayAdapter?.notifyDataSetChanged()
                }
            }
            Toast.makeText(this@BluetoothLeActivity, "Device found: (" + deviceNames.size + ") - "+ result.device.name, Toast.LENGTH_SHORT).show()

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
     * Android Q check (and enable if not already done) location permissions needed for BLE
     * source: https://medium.com/google-developer-experts/exploring-android-q-location-permissions-64d312b0e2e1
     */
    private fun checkPermission(){
        val hasLocationPermission = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            val hasBackgroundLocationPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (hasBackgroundLocationPermission) {
                val hasFineLocationPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if(hasFineLocationPermission){

                }else{
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_BLE_LOCATION)
                }

            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_BLE_LOCATION)
            }

        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_BLE_LOCATION)
        }
    }

} //class

