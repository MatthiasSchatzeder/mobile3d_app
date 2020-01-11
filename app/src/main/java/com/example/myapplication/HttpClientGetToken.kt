package com.example.myapplication

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpClientGetToken : AsyncTask<Void, Void, Any?>(){

    private val url = URL("http://192.168.83.16:4000/api/v1/auth/login")
    var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("aapp", "UTF-8") +
            "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode("test", "UTF-8")


    override fun doInBackground(vararg p0: Void?): Any? {

        var token = ""

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            val wr = OutputStreamWriter(outputStream)
            wr.write(reqParam)
            wr.flush()

            Log.e("test ", "response: $responseCode + $responseMessage")


            BufferedReader(InputStreamReader(inputStream)).use {
                var response: String = ""

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

        return token
    }

}