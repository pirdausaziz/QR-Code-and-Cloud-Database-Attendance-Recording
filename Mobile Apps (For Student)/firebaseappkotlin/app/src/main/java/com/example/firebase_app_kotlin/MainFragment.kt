package com.example.firebase_app_kotlin

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebase_app_kotlin.databinding.ActivityMainFragmentBinding
import org.w3c.dom.Text
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.firebase_app_kotlin.fragments.HomeFragment
import com.example.firebase_app_kotlin.fragments.RecordsFragment
import com.example.firebase_app_kotlin.fragments.ScannerFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainFragment : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val scannerFragment = ScannerFragment()
    private val recordsFragment = RecordsFragment()
    private var back_pressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment)
        replaceFragment(homeFragment)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item->
            when(item.itemId) {
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_scanner -> replaceFragment(scannerFragment)
                R.id.ic_records -> replaceFragment(recordsFragment)
            }
            true
        }
        var permissions = arrayOf(android.Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(this, permissions,0)
        permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permissions,0)

        /*bottomNavigation.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.navigation_home -> replaceFragment(homeFragment)
                R.id.navigation_scanner -> replaceFragment(scannerFragment)
                R.id.navigation_records -> replaceFragment(recordsFragment)
            }
            true
        }*/
        /*session = SharedPrefManager(applicationContext)

        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_scanner, R.id.navigation_records
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.hide()
*/
/*        val sharedPrefFile = "LoginActivity"
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id","")
        val name = sharedPreferences.getString("name","")


        id_name = findViewById<TextView>(R.id.welcome_message)
        println("The result is:" + name)

        if (name != "") {
            id_name.setText("Welcome,\n " + name)
        } else {
            if (id != null) {
                read_text(id)
            }
        }


        btn_logout = findViewById<Button>(R.id.btn_logout)
        btn_logout.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                *//*val preferences = getSharedPreferences("LoginActivity", MODE_PRIVATE)
                val editor = preferences.edit()
                editor.clear()
                editor.apply()*//*
                session.LogoutUser()
                *//*intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()*//*

            }
        })*/
        supportActionBar?.hide()
    }
    override fun onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true)
            super.onBackPressed()
        } else {
            Toast.makeText(baseContext, "Press once again to exit!", Toast.LENGTH_SHORT).show()
            back_pressed = System.currentTimeMillis()
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        if(fragment!=null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment).commit()
            //transaction.commit()
        }
    }

    /*fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState!!)

        //Save the fragment's state here
    }*/


    /*fun read_text(email_id:String) {
        db.collection("Student").document(email_id).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(ContentValues.TAG, "Current data: ${snapshot.data?.get("name")}")
                val name = snapshot.data?.get("name")
                val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("name", name as String?)
                    apply()
                    lateinit var id_name:TextView
                    id_name = findViewById<TextView>(R.id.welcome_message)
                    id_name.setText("Welcome,\n " + name)
                }


            } else {
                Log.d(ContentValues.TAG, "Current data: null")
                Toast.makeText(this, "Invalid Account", Toast.LENGTH_LONG)
            }
        }
    }*/

}