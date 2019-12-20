package com.example.myapplication

import android.bluetooth.*
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_gatt_connect.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolbar
import java.util.*
import kotlin.collections.ArrayList


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
        //bluetoothGatt?.disconnect()

        //for debugging purposes
        bluetoothGatt?.close() // -> should be called in gattCallback onConnectionStateChanged(STATE_DISCONNECTED)

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
        bluetoothGatt = device?.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE)

        Toast.makeText(this, "" + bluetoothGatt?.device?.name, Toast.LENGTH_SHORT).show()


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
            getNetworks()
        }

    } //onCreate


    /**
     * callback from the GATT server
     */
    private val gattCallback = object: BluetoothGattCallback(){

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when(newState){
                BluetoothProfile.STATE_CONNECTED ->{
                    Toast.makeText(this@GattConnectActivity, "Connected to GATT", Toast.LENGTH_SHORT).show()
                }
                BluetoothProfile.STATE_DISCONNECTED ->{
                    bluetoothGatt?.close()

                    var builder: AlertDialog.Builder = AlertDialog.Builder(this@GattConnectActivity)
                    builder.setMessage("There was an error with the GATT connection ...")
                        .setCancelable(false)
                        .setPositiveButton("OK"){ _: DialogInterface, _: Int ->
                            finish()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Toast.makeText(this@GattConnectActivity, "" + bluetoothGatt?.services?.size, Toast.LENGTH_SHORT).show()

            bluetoothGatt?.services?.forEach{
                Toast.makeText(this@GattConnectActivity, "" + it.uuid, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            Toast.makeText(this@GattConnectActivity, "changed", Toast.LENGTH_SHORT).show()
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Toast.makeText(this@GattConnectActivity, "read", Toast.LENGTH_SHORT).show()
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Toast.makeText(this@GattConnectActivity, "write", Toast.LENGTH_SHORT).show()
        }
    } //gattCallback

    private fun getNetworks(){
        val byteArray: ByteArray = "{\"c\": 0}\n".toByteArray() //size is 9 byte

        wirelessCommander?.value = byteArray

        bluetoothGatt?.writeCharacteristic(wirelessCommander)
    }
}
