package com.example.myapplication


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_tool.*
import kotlinx.android.synthetic.main.fragment_tool.view.*
import kotlinx.android.synthetic.main.fragment_tool.view.text_input_distance
import kotlin.math.absoluteValue


class Tool : Fragment() {


    val toolList = listOf<String>("E 1", "E 2", "E 3")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tool, container, false)

        view.tool_dropdown.keyListener = null   //makes the textfield non editable (you can only select from the given items)

        val adapter: ArrayAdapter<String> = ArrayAdapter(context as Context, R.layout.tool_dropdown_item, toolList)
        view.tool_dropdown.setAdapter(adapter)

        var tool: Any? = null
        view.tool_dropdown.setOnItemClickListener(AdapterView.OnItemClickListener() { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            tool = adapterView.getItemAtPosition(i)
            //Toast.makeText(context, "$tool", Toast.LENGTH_SHORT).show()
        })

        view.btn_extrude.setOnClickListener {
            val distance: Int = text_input_distance.text.toString().toInt()

            Toast.makeText(context, "extruding $tool for " + distance + "mm", Toast.LENGTH_SHORT).show()
        }

        return view
    }

}
