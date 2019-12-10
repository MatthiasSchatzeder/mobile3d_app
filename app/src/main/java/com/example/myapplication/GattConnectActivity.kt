package com.example.myapplication

import android.bluetooth.*
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_settings.*

class GattConnectActivity : AppCompatActivity() {

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

        Toast.makeText(this, "" + device?.name, Toast.LENGTH_SHORT).show()

        /**
         * get BluetoothGATT
         * connection Object
         */
        val bluetoothGatt = device?.connectGatt(this, false, gattCallback)

    } //onCreate

    /**
     * callback from the GATT server
     */
    private val gattCallback = object: BluetoothGattCallback(){

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when(newState){
                BluetoothProfile.STATE_CONNECTED ->{
                    Toast.makeText(this@GattConnectActivity, "Connectted to GATT", Toast.LENGTH_SHORT).show()
                }
                BluetoothProfile.STATE_DISCONNECTED ->{

                    var builder: AlertDialog.Builder = AlertDialog.Builder(this@GattConnectActivity)
                    builder.setMessage("There was an error with GATT connection ...")
                        .setCancelable(false)
                        .setPositiveButton("OK"){ _: DialogInterface, _: Int ->
                            finish()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }
        }


        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }
    }
}
