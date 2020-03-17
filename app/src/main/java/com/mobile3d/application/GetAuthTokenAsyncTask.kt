package com.mobile3d.application

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class GetAuthTokenAsyncTask : AsyncTask<String, Void, Any?>() {

    /**
     * reqParam are the params for the HTTP request
     *
     * in order to get a bearer token the client has to log in. Here the login name and password for the app are used.
     */
    var reqParam =
        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("aapp", "UTF-8") +
                "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(
            "test",
            "UTF-8"
        )

    override fun doInBackground(vararg p0: String): String? {

        //url of the backend auth -> needed to get the auth bearer token
        val authURL = URL("http://${p0[0]}:4000/api/v1/auth/login")

        Log.e("test ", "post request to this url $authURL")

        try {

            /**
             * get auth token with http post request
             */
            with(authURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                connectTimeout = 500   //set connect timeout in ms

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
                    MyAuthToken = JSONObject(response).getString("token")

                    /**
                     * writes auth token to the sharedPreference
                     * editor.apply instead of .commit to prevent ui thread from freezing -> commits changes in another thread or async task
                     */
                    val editor = SharedPref!!.edit()
                    editor.putString("auth", JSONObject(response).getString("token"))
                    editor.apply()

                    return JSONObject(response).getString("token")
                }
            }

        } catch (exc: Exception) {
            Log.e("test ", "URL not reachable")
            return null
        }


    }   //doInBackground


}