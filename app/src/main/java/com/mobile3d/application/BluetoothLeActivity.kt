package com.mobile3d.application

import android.Manifest
import android.app.Activity
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
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_bluetooth_le.*


private const val SCAN_PERIOD: Long = 10000
private const val ENABLE_BT_REQUEST_CODE = 1

private const val REQUEST_CODE_BLE_LOCATION = 32
private const val CONNECTION_LOST_REQUEST = 40

class BluetoothLeActivity: AppCompatActivity() {


    //variables
    private val handler: Handler = Handler()
    private var currentlyScanning: Boolean = false

    var filters = ArrayList<ScanFilter>()

    var devices = ArrayList<BluetoothDevice>()
    var deviceNames = ArrayList<String>()

    //declare array adapter
    var arrayAdapter: ArrayAdapter<String>? = null
    //declare bluetooth adapter
    private var bluetoothAdapter: BluetoothAdapter? = null
    //declare location manager
    private var locationManager: LocationManager? = null

    /**
     * stop scanning if the user leaves the scanning screen
     */
    override fun onStop() {
        if(currentlyScanning){
            scanLeDevices(false)
        }

        super.onStop()
    }

    override fun onResume() {
        deviceNames.clear()
        devices.clear()
        arrayAdapter?.notifyDataSetChanged()

        super.onResume()
    }


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
         * used to enable bluetooth, get BLE scanner
         */
        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        /**
         * get location manager
         * used to check if GPS is enabled
         */
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        /**
         * link ListView and array adapter
         */
        arrayAdapter = ArrayAdapter(this, R.layout.bluetooth_le_device_list_item, deviceNames)
        listView_bluetoothLeDevices.adapter = arrayAdapter

        /**
         * click listener on list view items
         */
        listView_bluetoothLeDevices.setOnItemClickListener { _, _, position, _ ->
            //Toast.makeText(this, "item nr" + position + " with name "+ devices[position].name,Toast.LENGTH_SHORT).show()

            if(currentlyScanning){
                scanLeDevices(false)
            }

            var targetBtDevice: BluetoothDevice = devices[position]

            val intent = Intent(this, GattOperationsActivity::class.java)
            intent.putExtra("BleDevice", targetBtDevice)
            startActivityForResult(intent, CONNECTION_LOST_REQUEST)
        }

        /**
         * btn to start scan method
         */
        btn_scanDevices.setOnClickListener{
            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
                if (currentlyScanning) {
                    Toast.makeText(this, "already scanning ...", Toast.LENGTH_SHORT).show()
                } else {
                    devices.clear()
                    deviceNames.clear()
                    arrayAdapter?.notifyDataSetChanged()

                    scanLeDevices(true)
                }
            }else{
                /**
                 * user request to enable GPS if not already done
                 */
                checkEnableGPS()
            }
        } //btn listener

        /**
         * check permission function call
         */
        checkPermission()

        /**
         * function call to check for BT
         */
        checkEnableBT()



    } //OnCreate

    /**
     * enable Bluetooth request if not already enabled
     */
    private fun checkEnableBT(){
        if(bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_CODE)
        }
    }

    /**
     * checks if GPS is enabled
     * if not opens an alert dialog that informs the user to enable GPS and open the gps enable page in the android settings
     */
    private fun checkEnableGPS(){
        if (!locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
        var builder = MaterialAlertDialogBuilder(this)
            .setMessage("GPS is disabled!\nScanning for devices does not work without location tracking enabled")
            .setCancelable(false)
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
        }
        val alert = builder.create()
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
        else if(requestCode == CONNECTION_LOST_REQUEST && resultCode == Activity.RESULT_OK){

            Log.e("result", "" + data?.getStringExtra("result"))

            if(data?.getStringExtra("result").equals("connection_lost")) {
                /**
                 * Alert Dialog
                 */
                val builder = MaterialAlertDialogBuilder(this)
                    .setMessage("Connection lost, try again.")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                        //nothing else happens
                    }
                val alert = builder.create()
                alert.show()
            }else if(data?.getStringExtra("result").equals("wrong_device")){
                val builder = MaterialAlertDialogBuilder(this)
                    .setMessage("device is not supported")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                        //nothing else happens
                    }
                val alert = builder.create()
                alert.show()
            }
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
                //Toast.makeText(this, "" + devices.size + " devices found",Toast.LENGTH_SHORT).show()
                btn_scanDevices.text = "SCAN FOR DEVICES"
                progress_bar.visibility = View.INVISIBLE
            }, SCAN_PERIOD)

            currentlyScanning = true
            //bluetoothLeScanner?.startScan(filters, ScanSettings.Builder().setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build(), myLeScanCallback)
            bluetoothLeScanner?.startScan(myLeScanCallback)

            btn_scanDevices.text = "scanning ..."
            progress_bar.visibility = View.VISIBLE
        } else {
            currentlyScanning = false
            bluetoothLeScanner?.stopScan(myLeScanCallback)
            btn_scanDevices.text = "SCAN FOR DEVICES"
            progress_bar.visibility = View.INVISIBLE
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
            //debugging toast
            //Toast.makeText(this@BluetoothLeActivity, "Device found: (" + deviceNames.size + ") - "+ result.device.name, Toast.LENGTH_SHORT).show()
        }
        // not needed rn
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
     * Android permission check (get permission if not already granted) location permissions needed for BLE scanner
     * ACCESS_BACKGROUND_LOCATION is needed for android Q (API 29)
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

