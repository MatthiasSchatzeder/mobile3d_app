package com.example.myapplication

import android.app.Activity
import android.bluetooth.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_gatt_connect.*
import kotlinx.android.synthetic.main.activity_settings.toolbar


var bluetoothGatt: BluetoothGatt? = null

/**
 * declare bluetooth gatt service and characteristics
 * wirelessService:     The Wireless Service allows a client to configure and monitor a wireless network connection
 * wirelessCommander:   The connection can be controlled with the Wireless commander characteristic
 * commanderResponse:   Each command sent will generate a response on the Commander response characteristic containing the
 *                      error code for the command
 */
var wirelessService: BluetoothGattService? = null
var wirelessCommander: BluetoothGattCharacteristic? = null
var commanderResponse: BluetoothGattCharacteristic? = null


class GattConnectActivity : AppCompatActivity() {

    override fun onStop() {
        bluetoothGatt?.close()

        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gatt_connect)

        /**
         * toolbar setup
         */
        var toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Wifi Setup"
        supportActionBar?.subtitle = "Scan and Select the Wifi to connect to"
        toolbar.setNavigationOnClickListener{
            finish()
        }

        /**
         * get passed BT device from BluetoothLEActivity
         */
        val device: BluetoothDevice? = intent.getParcelableExtra("BleDevice")

        /**
         * get BluetoothGATT
         * connection Object
         */
        Log.e("Adress", device?.address.toString())

        bluetoothGatt = device?.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE)

        //Toast.makeText(this, "" + bluetoothGatt?.device?.name, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "" + bluetoothGatt.toString(), Toast.LENGTH_SHORT).show()

        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager // -> for testing, getting connected devices


        /**
         * get GATT services and characteristics with UUID
         */
        /*if(bluetoothGatt != null) {
            wirelessService = bluetoothGatt?.getService(UUID.fromString("e081fec0-f757-4449-b9c9-bfa83133f7fc"))
            if (wirelessService != null) {
                wirelessCommander = wirelessService?.getCharacteristic(UUID.fromString("e081fec1-f757-4449-b9c9-bfa83133f7fc"))
                commanderResponse = wirelessService?.getCharacteristic(UUID.fromString("e081fec2-f757-4449-b9c9-bfa83133f7fc"))

                if (wirelessCommander == null) {
                    Toast.makeText(
                        this,
                        "characteristic wirelessCommander not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (commanderResponse == null) {
                    Toast.makeText(this,"characteristic commanderResponse not available",Toast.LENGTH_SHORT).show()
                }

            } else {
                //service not available
                Toast.makeText(this, "service not available", Toast.LENGTH_SHORT).show()
            } //get service and characteristics with UUID
        }else{
            Toast.makeText(this, "bt gatt null", Toast.LENGTH_SHORT).show()
        }
         */

        /**
         * btn click listener
         */
        btn_getNetworks.setOnClickListener{
            //getNetworks()
            //bluetoothGatt?.discoverServices()
            bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).forEach{
                Log.e("device: ", it.name + " ; "+ it.address)
            }
        }



    } //onCreate

    private fun notifyConnectionLost(){
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Connection lost, try again.")
            .setCancelable(false)
            .setPositiveButton("OK"){ _: DialogInterface, _: Int ->
                finish()
            }
        val alert = builder.create()
        alert.show()
    }

    /**
     * callback from the GATT server
     */
    private val gattCallback = object: BluetoothGattCallback(){

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when(newState){
                BluetoothProfile.STATE_CONNECTED ->{
                    bluetoothGatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED ->{
                    bluetoothGatt?.close()
                    Log.e("ConStateChnages", "disconnected")

                    /**
                     * return
                     */
                    val returnIntent = Intent()
                    returnIntent.putExtra("result", "connection_lost")
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()

                }
            }

            Log.e("ConStateChnages", "$status $newState")
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

            bluetoothGatt?.services?.forEach{
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        }
    } //gattCallback

    private fun getNetworks(){
        val byteArray: ByteArray = "{\"c\": 0}\n".toByteArray() //size is 9 byte

        wirelessCommander?.value = byteArray

        bluetoothGatt?.writeCharacteristic(wirelessCommander)
    }
}
