package com.example.myapplication

import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_socket_setup.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ConnectSocketAsyncTask : AsyncTask<String, Void, Any?>(){


    /**
     * reqParam are the params for the HTTP request
     */
    var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("aapp", "UTF-8") +
            "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode("test", "UTF-8")


    override fun doInBackground(vararg p0: String): Any? {

        //url of the backend auth -> needed to get the auth bearer token
        val authURL = URL("http://${p0[0]}:4000/api/v1/auth/login")

        //url of the backend -> needed for the socketIO connection
        val socketURL = "http://${p0[0]}:4000"

        //bearer auth token -> needed for the socketIO connection
        var token = ""

        Log.e("test ", "post request to this url $authURL")

        try {

            /**
             * get auth token with http post request
             */
            with(authURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                connectTimeout = 2000   //set connect timeout to 5s

                val wr = OutputStreamWriter(outputStream)
                wr.write(reqParam)
                wr.flush()

                Log.e("test ", "response: $responseCode + $responseMessage")

                BufferedReader(InputStreamReader(inputStream)).use {
                    var response = ""

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response += inputLine
                        inputLine = it.readLine()
                    }
                    it.close()

                    token = JSONObject(response).getString("token")
                    //Log.e("test ", token)
                }
            }

            /**
             * open socket io connection
             */
            val opts = IO.Options()
            opts.query = "token=Bearer $token"
            Log.e("test ", "" + opts.query)
            val socket: Socket = IO.socket(socketURL, opts)

            socket.connect()
                .on(Socket.EVENT_CONNECT) {
                    Log.e("test ", "connected")
                }
                .on(Socket.EVENT_DISCONNECT) {
                    Log.e("test ", "disconnected")
                }

            return socket

        }catch (exc: Exception){
            Log.e("test ", "URL not reachable")
            return null
        }


    }   //doInBackground


}