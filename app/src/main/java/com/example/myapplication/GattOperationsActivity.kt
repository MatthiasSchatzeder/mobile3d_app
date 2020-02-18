package com.example.myapplication

import android.app.Activity
import android.bluetooth.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_gatt_connect.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class GattOperationsActivity : AppCompatActivity() {

    var bluetoothGatt: BluetoothGatt? = null
    /**
     * declare bluetooth gatt service and characteristics
     * @wirelessService:    The Wireless Service allows a client to configure and monitor a wireless network connection
     * @wirelessCommander:  The connection can be controlled with the Wireless commander characteristic
     * @commanderResponse:  Each command sent to the wirelessCommander will generate a response on the CommanderResponse
     *                      characteristic containing the error code for the command and callback message
     * @commanderResponseDescriptor:    is used to enable callback messages on the server
     */
    var wirelessService: BluetoothGattService? = null
    var wirelessCommander: BluetoothGattCharacteristic? = null
    var commanderResponse: BluetoothGattCharacteristic? = null
    var commanderResponseDescriptor: BluetoothGattDescriptor? = null

    var sendingQueue: Queue<String> = LinkedList()
    var callbackMsg: String = ""

    private var networkNames = ArrayList<String>()

    //declare array adapter
    private var arrayAdapter: ArrayAdapter<String>? = null

    /**
     * 0: loading / connecting
     * 1: connected / idle
     * 2: sending
     */
    private var myStatus: Int = 0

    private var isConnectingToWifi = false

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
        val toolbar = toolbar
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

        listView_networks.setOnItemClickListener{_, _, position, _ ->
            textInput_ssid.setText(networkNames[position])
        }

        /**
         * btn connect on click listener
         */
        btn_connect.setOnClickListener {
            isConnectingToWifi = true
            hideKeyboard(this)

            //testing
            //textInput_ssid.setText("ARRIS_24G")
            //textInput_password.setText("1chGebeN1emalsAuf!")

            if(textInput_ssid.text!!.isNotEmpty() && textInput_password.text!!.isNotEmpty()) {
                networkNames.clear()
                arrayAdapter?.notifyDataSetChanged()

                if (myStatus == 1) {
                    setLoading(true)

                    //connect to selected network
                    writeToWirelessCommander("{\"c\":1,\"p\":{\"e\":\"${textInput_ssid.text}\",\"p\":\"${textInput_password.text}\"}}\n")

                    textInput_ssid.text!!.clear()
                    textInput_password.text!!.clear()

                    /**
                     * coroutinescope is used to execute code async -> in order to siplay
                     *
                     * context is Main because an alert dialog is shown [Main is the context to use when UI operations are executed]
                     */
                    CoroutineScope(Main).launch {
                        //wait until sending is done
                        @Suppress("ControlFlowWithEmptyBody")
                        while (myStatus == 2);

                        var count = 0
                        do {
                            delay(500)

                            writeToWirelessCommander("{\"c\":5}\n")

                            //wait until sending is done
                            @Suppress("ControlFlowWithEmptyBody")
                            while (myStatus == 2);

                            count++
                        }while (!Patterns.IP_ADDRESS.matcher(JSONObject(callbackMsg).getJSONObject("p").getString("i")).matches() && count < 15)

                        showCurrentConnection()
                    }


                }
            }else{
                Toast.makeText(this, "enter password and ssid", Toast.LENGTH_SHORT).show()
            }
            isConnectingToWifi = false
        }

        btn_getConnection.setOnClickListener{
            if(!isConnectingToWifi) {
                hideKeyboard(this)

                writeToWirelessCommander("{\"c\":5}\n")

                //wait until sending is done
                @Suppress("ControlFlowWithEmptyBody")
                while (myStatus == 2);

                showCurrentConnection()

            }
        }

        /**
         * btn get networks on click listener
         */
        btn_getNetworks.setOnClickListener{
            hideKeyboard(this)

            networkNames.clear()
            writeToWirelessCommander("{\"c\":0}\n")

            //wait until sending is done
            @Suppress("ControlFlowWithEmptyBody")
            while (myStatus == 2);

            val networks = JSONObject(callbackMsg).getJSONArray("p")
            var i = 0
            if(networks.length() > 0) {
                while (i < networks.length()) {
                    val currNetwork: JSONObject = networks.getJSONObject(i)
                    if(currNetwork.getString("e").isNotEmpty()) {
                        networkNames.add(currNetwork.getString("e"))
                    }
                    i++
                }
            }else{
                Toast.makeText(this, "no visible networks in range", Toast.LENGTH_SHORT).show()
            }

            arrayAdapter?.notifyDataSetChanged()
        }


    } //onCreate

    /**
     * function that hides the keyboard
     */
    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * opens alert dialog with current connection information
     */
    private fun showCurrentConnection(){
    setLoading(false)

        if(callbackMsg.isNotEmpty()) {
            val myConnection = JSONObject(callbackMsg).getJSONObject("p")

            val text: String

            if (myConnection.getString("e").isEmpty() && myConnection.getString("i").isEmpty() ||
                !Patterns.IP_ADDRESS.matcher(myConnection.getString("i")).matches())
            {
                text = "No network connected at the moment.<br>Try to connect again and check password and ssid."
            } else {
                text = "WIFI: <b>" + myConnection.getString("e") + "</b> <br>" +
                        "IP: <b>" + myConnection.getString("i") + "</b> "

                //sets global variable to raspberry ip
                BackendIP = myConnection.getString("i")
            }

            val builder = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                .setTitle("Current Connection")
                .setMessage(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("OK"){ _, _ ->
                    //do nothing on OK
                }


            val alertDialog = builder.create()
            alertDialog.show()
        }else{
            Log.e("error", "showCurrentConnection: callback message is empty")
        }
    }

    /**
     * function that shows loading animation
     */
    private fun setLoading(enabled: Boolean){
        if(enabled){
            progress_bar.visibility = View.VISIBLE //enable progress bar
            loading_view.visibility = View.VISIBLE //enable grey background
            myStatus = 0
        }else{
            progress_bar.visibility = View.INVISIBLE //disable progress bar
            loading_view.visibility = View.INVISIBLE //disable grey background
            myStatus = 1
        }
    }

    /**
     * return to previous activity with connection_lost as result value
     */
    private fun returnConnectionLost(msg: String){
        val returnIntent = Intent()
        returnIntent.putExtra("result", msg)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    /**
     * splits string into smaller parts and adds it to the sendingQueue
     * chunked: splits string and returns map that can be iterated and the split string parts are added to sendingQueue
     *          needs to be done (bluetooth gatt can only communicate with 20Bytes per message, size of 10Bytes per message is used for safer communication)
     */
    private fun writeToWirelessCommander(string: String){
        callbackMsg = ""
        //setLoading(true)

        //checks if there is currently no sending operation going on
        if(sendingQueue.isEmpty() || myStatus == 1) {
            myStatus = 2

            //splits the String into 10 byte packages and adds them to the sending queue
            string.chunked(10).forEach {
                sendingQueue.add(it)
            }

            //value is set to the first object of the sending queue
            //.remove() takes out the first object of the sending queue
            wirelessCommander?.value = sendingQueue.remove().toByteArray()

            //if writeCharacteristic() returns false the message cant be written to the characteristic / or sent to the server
            //then there was an error with the connection and the user should connect again. -> return to the previous activity
            if(bluetoothGatt?.writeCharacteristic(wirelessCommander) == false){
                returnConnectionLost("connection_lost")
            }

        }else{
            Log.e("GattCallback ", "sendingQueue ist not empty | status is sending(2)")
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
                    returnConnectionLost("connection_lost")
                }
            }
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

                    /**
                     * write '0100' to the descriptor of the commanderResponse characteristic to enable response callbacks
                     * on value change
                     */
                    if(commanderResponseDescriptor != null){
                        commanderResponseDescriptor!!.value = byteArrayOf(0x01, 0x00)
                        bluetoothGatt?.writeDescriptor(commanderResponseDescriptor)
                    }
                }


                //disables loading animation
                setLoading(false)
            }else{
                returnConnectionLost("wrong_device")
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            Log.e("GattCallback ", "descriptor write: $status")
        }

        /**
         * callback if characteristic value changed => callback message from the server
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {

            val s = characteristic!!.value!!.toString(Charsets.UTF_8)

            if(s.contains("\n")){   //if callback contains '\n' it is the last package of 1 message
                callbackMsg += s

                Log.e("GattCallback ", callbackMsg)

                val myJSONObject = JSONObject(callbackMsg)  //create JSON object from the callback message

                when {
                    myJSONObject.getInt("c") == 0 -> { //getNetworks was called

                        val networks: JSONArray = myJSONObject.getJSONArray("p") //get JSON array of networks

                        //debugging output
                        var i = 0
                        while (i < networks.length()) {
                            val currNetwork: JSONObject = networks.getJSONObject(i)
                            Log.e("GattCallback ", currNetwork.getString("e"))
                            i++
                        }
                        //setLoading(false)
                    }
                    myJSONObject.getInt("c") == 1 -> { //connect to wifi was called

                        //TODO
                        //added some extra delay for raspi to connect
                        /*
                        handler.postDelayed({
                            setLoading(false)
                        },2000)

                         */
                    }
                    myJSONObject.getInt("c") == 5 -> { //get connection was called

                        //debugging output
                        val myConnection = myJSONObject.getJSONObject("p")
                        Log.e("GattCallback ", myConnection.getString("i") + "<- ip here if not empty")
                        //setLoading(false)
                    }
                }

                myStatus = 1

            }else{
                callbackMsg += s
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Log.e("GattCallback ", "read $status")
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Log.e("GattCallback ", "writeCallback $status")

            //send next package if sending queue is not empty AND
            //status from the last send operation is 0 (-> operation was successful)
            //TODO: error handling if last send operation was not successful (status != 0)
            if(!sendingQueue.isEmpty() && status == 0){
                wirelessCommander?.value = sendingQueue.remove().toByteArray()
                bluetoothGatt?.writeCharacteristic(wirelessCommander)
            }
        }
    } //gattCallback
}
