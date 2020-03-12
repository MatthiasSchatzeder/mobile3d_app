package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_password_ip.*

class PasswordIpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_ip)

        /**
         * toolbar init
         */
        val toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "WifiSetup"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val ssid = intent.getStringExtra("ssid")
        textView_ssid.text = ssid

        /**
         * on connect click listener
         */
        btn_connect.setOnClickListener{
            val password = textInput_password.text.toString()
            var error = 0
            val returnIntent = Intent()

            if(password.isEmpty()){
                Snackbar.make(it, "password cant be empty", Snackbar.LENGTH_SHORT).show()
                error = 1
            }else{
                returnIntent.putExtra("password", password)
            }

            returnIntent.putExtra("ssid", ssid)

            if(error == 0){
                hideKeyboard(this)
                // finish and return password and ssid with intent extras
                val myReturnIntent = Intent()
                setResult(Activity.RESULT_OK, myReturnIntent)
                myReturnIntent.putExtra("ssid", ssid)
                myReturnIntent.putExtra("password", password)
                setResult(Activity.RESULT_OK, myReturnIntent)

                finish()
                overridePendingTransition(0,0)
            }

        }

    } //onCreate

    /**
     * function that hides the keyboard
     */
    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * initializes sidebar menu layout
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}
