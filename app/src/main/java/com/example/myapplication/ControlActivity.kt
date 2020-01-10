package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_control.*
import java.io.*
import java.lang.StringBuilder
import java.lang.reflect.Parameter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class ControlActivity : AppCompatActivity() {

    val url = "http://192.168.83.16"
    var params: Map<String, String> = HashMap()
    var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("app", "UTF-8") +
    "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode("test", "UTF-8")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        var adapter = FragmentPagerAdapter(supportFragmentManager, lifecycle)

        adapter.addFragment(Axes())
        adapter.addFragment(Tool())
        adapter.addFragment(General())

        viewpager.adapter = adapter

        TabLayoutMediator(tabs, viewpager, object: TabLayoutMediator.OnConfigureTabCallback{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position){
                    0 -> tab.setText("Axis")
                    1 -> tab.setText("Tool")
                    2 -> tab.setText("General")
                }
            }
        }).attach()

        //init toolbar
        var toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Control"

        /**
         * listens to clicks of the Navigation Icon
         */
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })


        /**
         * http post request to get auth token from api / backend
         */
        var url_object = URL(url)
        var con: HttpURLConnection = url_object.openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.doOutput = true


        var outStream = OutputStreamWriter(con.outputStream)
        outStream.write(reqParam)
        outStream.flush()

        /**
         * response
         */
        var inStream = BufferedInputStream(con.inputStream)
        var bufferedReader = BufferedReader(InputStreamReader(inStream))
        var result = ""

        var line:String
        do {
            line = bufferedReader.readLine()
            result += line

        }while (line != null)

        Log.e("test ", "" + result)

    }// onCreate


    /**
     * toolbar menu init
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.action_menu){
            Toast.makeText(this, "right icon pressed", Toast.LENGTH_SHORT).show()

            return true
        }


        return super.onOptionsItemSelected(item)
    }

}
