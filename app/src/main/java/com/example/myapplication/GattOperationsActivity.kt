package com.example.myapplication

import android.app.Activity
import android.bluetooth.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_gatt_connect.*
import org.json.JSONArray
import org.json.JSONObject
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


class GattOperationsActivity : AppCompatActivity() {

    var wirelessService: BluetoothGattService? = null
    var wirelessCommander: BluetoothGattCharacteristic? = null
    var commanderResponse: BluetoothGattCharacteristic? = null
    var commanderResponseDescriptor: BluetoothGattDescriptor? = null

    var sendingQueue: Queue<String> = LinkedList()
    var callbackMsg: String = ""

    var networkNames = ArrayList<String>()

    //declare array adapter
    var arrayAdapter: ArrayAdapter<String>? = null

    /**
     * 0: loading / connecting
     * 1: done / connected
     */
    var myStatus: Int = 0


    override fun onStop() {
        bluetoothGatt?.close()
        networkNames.clear()

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
        toolbar.setNavigationOnClickListener{
            finish()
        }

        /**
         * link ListView and array adapter
         */
        arrayAdapter = ArrayAdapter(this, R.layout.networks_list_item, networkNames)
        listView_networks.adapter = arrayAdapter

        /**
         * get passed BT device from BluetoothLEActivity
         */
        val device: BluetoothDevice? = intent.getParcelableExtra("BleDevice")

        /**
         * get BluetoothGATT
         * connection Object
         */
        bluetoothGatt = device?.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE)

        //enables loading animation
        setLoading(true)

        //btn_getNetworks.isEnabled = false
        btn_getNetworks.text = "get available networks\n [being developed]"

        //val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager // -> for testing, getting connected devices

        /**
         * btn click listener
         */
        btn_getNetworks.setOnClickListener{
            if(myStatus == 1){ //checks if the gatt is connected and characteristics are initialized
                getNetworks()
                //writeToWirelessCommander("{\"c\":5}\n")
            }
        }

        btn_connect.setOnClickListener {

            textInput_ssid.setText("ARRIS_24G")
            textInput_password.setText("1chGebeN1emalsAuf!")

            if(!textInput_ssid.text!!.isEmpty() && !textInput_password.text!!.isEmpty()) {
                if (sendingQueue.isEmpty()) {
                    writeToWirelessCommander("{\"c\":1,\"p\":{\"e\":\"${textInput_ssid.text}\",\"p\":\"${textInput_password.text}\"}}\n")

                    textInput_ssid.text!!.clear()
                    textInput_password.text!!.clear()
                }
            }else{
                Toast.makeText(this, "enter password and ssid", Toast.LENGTH_SHORT).show()
            }
        }

    } //onCreate

    private fun setLoading(enabled: Boolean){
        if(enabled){
            progress_bar.visibility = View.VISIBLE //enable progress bar
            grey_out_view.visibility = View.VISIBLE //enable grey background
            myStatus = 0
        }else{
            progress_bar.visibility = View.INVISIBLE //disable progress bar
            grey_out_view.visibility = View.INVISIBLE //disable grey background
            myStatus = 1
        }
    }

    private fun returnConnectionLost(){
        val returnIntent = Intent()
        returnIntent.putExtra("result", "connection_lost")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    /**
     * splits string into smaller parts and adds it to the sendingQueue
     * chunked: splits string and returns map that can be iterated and the split string parts are added to sendingQueue
     *          needs to be done (bluetooth gatt can only communicate with 20Bytes per message, size of 10(Bytes) is used for safer communication)
     */
    private fun writeToWirelessCommander(string: String){
        if(sendingQueue.isEmpty()) {
            string.chunked(10).forEach {
                sendingQueue.add(it)
            }

            wirelessCommander?.value = sendingQueue.remove().toByteArray()
            bluetoothGatt?.writeCharacteristic(wirelessCommander)
        }else{
            Log.e("GattCallback ", "sendingQueue ist not empty")
        }
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

                    /**
                     * return to scan activity and pass connection lost argument
                     */
                    returnConnectionLost()
                }
            }

            Log.e("ConStateChnages", "$status $newState")
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

            //debugging
            bluetoothGatt?.services?.forEach{
                Log.e("service: ", it.uuid.toString() + " ; " + it.type)
            }

            /**
             * get GATT services and characteristics with UUID
             */
            wirelessService = bluetoothGatt?.getService(UUID.fromString("e081fec0-f757-4449-b9c9-bfa83133f7fc"))

            if(wirelessService != null){
                Log.e("wirelessService ", "ready")

                wirelessCommander = wirelessService?.getCharacteristic(UUID.fromString("e081fec1-f757-4449-b9c9-bfa83133f7fc"))
                commanderResponse = wirelessService?.getCharacteristic(UUID.fromString("e081fec2-f757-4449-b9c9-bfa83133f7fc"))

                if(wirelessCommander != null){
                    Log.e("WirelessCommander ", "ready")
                }

                if(commanderResponse != null){
                    Log.e("CommanderResponse ", "ready")

                    bluetoothGatt?.setCharacteristicNotification(commanderResponse, true)

                    commanderResponseDescriptor = commanderResponse!!.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))

                    if(commanderResponseDescriptor != null){
                        commanderResponseDescriptor!!.value = byteArrayOf(0x01, 0x00)
                        Log.e("GattCallback ", "" + bluetoothGatt?.writeDescriptor(commanderResponseDescriptor))
                    }
                }


                //disables loading animation
                setLoading(false)
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            Log.e("GattCallback ", "descriptor write: $status")
        }

        /**
         * callback information
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            //Log.e("GattCallback ", "characteristic changed(${characteristic?.uuid}): ${characteristic?.value?.toString(Charsets.UTF_8)}")

            var s = characteristic!!.value!!.toString(Charsets.UTF_8)

            if(s.contains("\n")){
                callbackMsg += s

                Log.e("GattCallback ", callbackMsg)

                val myJSONObject = JSONObject(callbackMsg)

                if(myJSONObject.getInt("c") == 0) { //getNetworks was called
                    val networks: JSONArray = myJSONObject.getJSONArray("p")
                    /**
                     * add networks to networkNames
                     */
                    var i = 0
                    while (i < networks.length()) {
                        var currNetwork: JSONObject = networks.getJSONObject(i)
                        networkNames.add(currNetwork.getString("e"))
                        Log.e("GattCallback ", currNetwork.getString("e"))
                        i++
                    }
                }
                else if(myJSONObject.getInt("c") == 1){ //connect was called
                    //cant be called here -> not response, but dont know why
                    //writeToWirelessCommander("{\"c\":5}\n")
                }
                else if(myJSONObject.getInt("c") == 5){ //get connection was called
                    var myConnection = myJSONObject.getJSONObject("p")
                    Log.e("GattCallback ", myConnection.getString("i") + "<- ip here if not empty")
                }

                callbackMsg = ""
            }else{
                callbackMsg += s
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Log.e("GattCallback ", "read $status")
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Log.e("GattCallback ", "writeCallback $status")

            if(!sendingQueue.isEmpty() && status == 0){
                wirelessCommander?.value = sendingQueue.remove().toByteArray()
                bluetoothGatt?.writeCharacteristic(wirelessCommander)
            }
        }

    } //gattCallback

    private fun getNetworks(){
        //networkNames.clear()
        //arrayAdapter?.notifyDataSetChanged()
        //Log.e("GattCallback ", "" + networkNames.size)

        //wirelessCommander?.value = "{\"c\":0}\n".toByteArray() //size is 9 byte
        writeToWirelessCommander("{\"c\":0}\n")

        /*
        if(bluetoothGatt?.writeCharacteristic(wirelessCommander)!!){
            //Log.e("GattCallback ", "true")
        }else{
            Toast.makeText(this, "Connection not responding, try to connect again.", Toast.LENGTH_SHORT).show()
        }

         */
    }
}
