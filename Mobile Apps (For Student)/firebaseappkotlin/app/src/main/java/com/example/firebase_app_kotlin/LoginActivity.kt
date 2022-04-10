package com.example.firebase_app_kotlin

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.ArithmeticException
import java.lang.Exception

class LoginActivity:AppCompatActivity() {

    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btn_login: Button
    lateinit var session: SharedPrefManager
    val MIN_PASSWORD_LEGTH = 6
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_app)
        supportActionBar?.hide()

        etEmail = findViewById<EditText>(R.id.et_email)
        etPassword = findViewById<EditText>(R.id.et_password)

        btn_login = findViewById<Button>(R.id.btn_login)
        session = SharedPrefManager(applicationContext)
        val result = session.checkLogin()
        println("This is the result: " + result.toString())
        //println("The results is: " + result)
        if (result == true) {
            intent = Intent(applicationContext, MainFragment::class.java)
            startActivity(intent)
            this.finish()
        }

        btn_login.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View): Unit {

                if(result==false) {
                    validateInput()
                } else {
                    intent = Intent(applicationContext, MainFragment::class.java)
                    startActivity(intent)
                }
                hideKeyboard()
            }
        })
        supportActionBar?.hide()
    }

    fun check_local(): String? {
        val sharedPrefFile = "LoginActivity"
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val defaultValue = sharedPreferences.getString("id","")
        println("The results is: " + defaultValue)
        return defaultValue
    }

    fun validateInput() {

        var state:String? = null
        var email_id = etEmail.text.toString().split("@")[0]
        //email_id = email_id.split("s")[1]
        //println("The ID is: "+email_id)
        if (etEmail.text.toString()=="") {
            etEmail.error = "Please enter your email"
            println(etEmail.error)
        }
        if (etPassword.text.toString()=="") {
            etPassword.error = "Please enter your password"

        }
        if (!isEmailValid(etEmail.text.toString())) {
            etEmail.error = "Please enter a valid email"

        }
        if(email_id=="") {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG)
        } else {
            db.collection("Student").document(email_id).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: ${snapshot.data}")
                    val name    = snapshot.data?.get("name").toString()
                    val id      = snapshot.data?.get("id").toString()
                    Log.d(TAG, "Current name: ${name}")
                    Log.d(TAG, "Current id: ${id}")
                    session.createLoginSession(name, id)

                    intent = Intent(applicationContext, MainFragment::class.java)
                    startActivity(intent)
                    Toast.makeText(this,"Logged in!",Toast.LENGTH_SHORT).show()
                    this.finish()

                    /*val intent = Intent(this, MainFragment::class.java)
                    intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()*/

                } else {
                    Log.d(TAG, "Current data: null")
                    Toast.makeText(this, "INVALID ACCOUNT: Please check your email", Toast.LENGTH_LONG).show()
                }
            }
        }

        /*val citiesRef = db.collection("Student")
        val query = citiesRef.whereEqualTo("id", email_id)*/
        }

    fun isEmailValid(email: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hideKeyboard(){
        // since our app extends AppCompatActivity, it has access to context
        val imm=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // we have to tell hide the keyboard from what. inorder to do is we have to pass window token
        // all of our views,like message, name, button have access to same window token. since u have button
        imm.hideSoftInputFromWindow(btn_login.windowToken, 0)

    }

    /*fun performSignUp(v: View) {
        if (validateInput()) {

            val email = etEmail!!.text.toString()
            val password = etPassword!!.text.toString()
            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
        }
    }*/

    fun goToSignUp(v:View) {
        //val intent = Intent(this, signupActivity::class.java)
        //startActivity(intent)
    }
}

//------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------
/*btn_login.setOnClickListener {

            val check_id:Boolean = validateInput()

            if (check_id) {
                intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
            else {
                Toast.makeText(applicationContext,"TRY AGAIN!", Toast.LENGTH_SHORT)
            }
        }*/
//------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------
/*db.collection("Student").document(email_id)
.get()
    .addOnSuccessListener { document ->
        if (document != null) {
            Log.d(TAG, "DocumentSnapshot data: ${document.data?.get("name")}")
            Toast.makeText(applicationContext,"You successfully signed-in!", Toast.LENGTH_SHORT)
            intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

        } else {
            Log.d(TAG, "No such document")
            Toast.makeText(applicationContext,"No active account!", Toast.LENGTH_SHORT)
        }
    }
    .addOnFailureListener { exception ->
        Log.d(TAG, "get failed with ", exception)
        Toast.makeText(applicationContext,"Failed to get data!", Toast.LENGTH_SHORT)
    }*/
//val doc = db.collection("Student").document().equals(email_id)
//var colExists:Boolean = false
/*val docSnapshot = docRef.get().addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val doc: DocumentSnapshot = task.result!!
        colExists = doc.exists()
        println("The document is: " + colExists)
    }
}*/
//------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------