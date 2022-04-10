package com.example.firebase_app_kotlin.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_app_kotlin.*


class HomeFragment : Fragment() {

    lateinit var session: SharedPrefManager
    lateinit var db:FirebaseClass
    lateinit var btn_logout:Button
    lateinit var btn_refresh: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SharedPrefManager(requireActivity().applicationContext)
        db = FirebaseClass(requireActivity().applicationContext)

        val welcomeMessage = view.findViewById<TextView>(R.id.welcome_message)
        welcomeMessage.setText("Hello, Welcome\n"+ session.getUserDetails()["name"]!!.uppercase())
        println("The user info is the following: " + session.getUserDetails())

        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        val subjectScanned = view.findViewById<TextView>(R.id.subject_scanned)
        val dateScanned = view.findViewById<TextView>(R.id.date_scanned)
        val timeScanned = view.findViewById<TextView>(R.id.time_scanned)
        val tempScanned = view.findViewById<TextView>(R.id.temperature_scanned)

        val lastScanned = session.getLastScanned()

        //println("The last scanned first: $lastScanned")
        var result = lastScanned["subject_scan"]
        //println("The result of last scanned is: $result")

        if (result==null){
            session.getUserDetails()["id"]?.let { db.getLastScanned(it) }

            val lastScanned = session.getLastScanned()
            subjectScanned.setText("Subject: "+lastScanned["subject_scan"])
            dateScanned.setText("Date: "+lastScanned["date_scan"])
            timeScanned.setText("Time: "+lastScanned["time_scan"])
            tempScanned.setText("Temperature: "+lastScanned["temp_scan"])

        }
        subjectScanned.setText("Subject: "+lastScanned["subject_scan"])
        dateScanned.setText("Date: "+lastScanned["date_scan"])
        timeScanned.setText("Time: "+lastScanned["time_scan"])
        tempScanned.setText("Temperature: "+lastScanned["temp_scan"])

        session.getUserDetails()["id"]?.let { db.getRecords(it) }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        btn_logout = view.findViewById<Button>(R.id.btn_logout)
        btn_logout.setOnClickListener {
            session.LogoutUser()
            Toast.makeText(context, "Logged out!",Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            val intent = Intent(requireActivity().applicationContext, LoginActivity::class.java)
            startActivity(intent)

        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        btn_refresh = view.findViewById<Button>(R.id.btn_refresh)
        btn_refresh.setOnClickListener {

            session.getUserDetails()["id"]?.let { db.getLastScanned(it) }
            session.getUserDetails()["id"]?.let { db.getRecords(it) }

            val lastScanned = session.getLastScanned()
            subjectScanned.setText("Subject: "+lastScanned["subject_scan"])
            dateScanned.setText("Date: "+lastScanned["date_scan"])
            timeScanned.setText("Time: "+lastScanned["time_scan"])
            tempScanned.setText("Temperature: "+lastScanned["temp_scan"])

        }



        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        //welcomeMessage.setText("Hello, welcome" + )
        /*val sharedPrefFile = "LoginActivity"
        val sharedPref: SharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val text_input = sharedPref.getString("text_input","")
        val text = view.findViewById<TextView>(R.id.home)
        text.setText(text_input)*/


        /*val home = view.findViewById<TextView>(R.id.home)
        home.setOnClickListener {
            Toast.makeText(context, "Welcome to home", Toast.LENGTH_SHORT).show()
        }*/
        /*val btn = view.findViewById<Button>(R.id.test_button)
        btn.setOnClickListener{
            text.setText(textword)
            with (sharedPref.edit()) {
                putString("text_input",textword)
                apply()
            }
        }*/
    }

}