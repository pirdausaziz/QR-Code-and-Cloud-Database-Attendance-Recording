package com.example.firebase_app_kotlin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}
/*
private fun hideKeyboard(){
    // since our app extends AppCompatActivity, it has access to context
    val imm=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // we have to tell hide the keyboard from what. inorder to do is we have to pass window token
    // all of our views,like message, name, button have access to same window token. since u have button
    imm.hideSoftInputFromWindow(button.windowToken, 0)

}*/