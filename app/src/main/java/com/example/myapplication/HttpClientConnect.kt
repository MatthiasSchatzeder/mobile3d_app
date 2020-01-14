package com.example.myapplication

import android.os.AsyncTask
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpClientConnect : AsyncTask<String, Void, Any?>(){


    var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("aapp", "UTF-8") +
            "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode("test", "UTF-8")


    override fun doInBackground(vararg p0: String): Any? {

        val url = URL("http://${p0[0]}:4000/api/v1/auth/login")
        var token = ""
        Log.e("test ", "post request to this url $url")


        try {

            /**
             * get auth token with http post request
             */
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

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
                    Log.e("test ", token)

                    GlobalAuthToken = token
                }
            }

            /**
             * open socket io connection
             */
            val opts = IO.Options()
            opts.query = "token=Bearer $token"
            Log.e("test ", "" + opts.query)
            val socket: Socket = IO.socket("http://192.168.83.16:4000", opts)

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
            return false
        }


    }

}