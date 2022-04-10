package com.example.firebase_app_kotlin

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.common.eventbus.SubscriberExceptionContext
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.Subject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class FirebaseClass {
    var db = Firebase.firestore
    var context: Context
    var session: SharedPrefManager

    constructor(context: Context){
        this.context = context
        this.session = SharedPrefManager(this.context)
    }

    fun getLastScanned(matric_num:String) {

        //val listener = db.collection("Student").document("s$matric_num")
        //db.collection("Student")

        db.collection("Student").document("s"+matric_num)
            .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(ContentValues.TAG, "Current data: ${snapshot.data}")
                val doc = snapshot.data
                val test = doc?.get("last_scanned")
                //println("The last scanned result is bla: $test")
                if (test==null) {
                    session.saveLastScanned("NULL", "NULL", "NULL", "NULL")
                } else {
                    val list = test as HashMap<*, *>
                    val scanned_val = getLastScannedInfo(list as HashMap<String, String>)
                    val subject = scanned_val["subject"].toString()
                    val date = scanned_val["date"].toString()
                    val time = scanned_val["time"].toString()
                    val temp = scanned_val["temperature"].toString()
                    session.saveLastScanned(subject, date, time, temp + "°C")
                }

            } else {
                Log.d(ContentValues.TAG, "Current data: null")
                Toast.makeText(context, "INVALID ACCOUNT: Please check your email", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun getLastScannedInfo(last_scanned: HashMap<String, String>):HashMap<String, String>  {

        var store: Map<String, String> = HashMap<String, String>()
        var timestamp = last_scanned["timestamp"] as com.google.firebase.Timestamp
        var milisec = timestamp.seconds*1000 + timestamp.nanoseconds/1000000 //'milisec' is a converted timestamp to milliseconds

        (store as HashMap).put("subject", last_scanned["subject"].toString())
        store.put("date",getDateTime("dd/MM/yyyy", milisec)) // from milliseconds to date
        store.put("time", getDateTime("HH:mm:ss", milisec)) // from milliseconds to time
        store.put("temperature", last_scanned["temperature"].toString())
        return store
    }

    fun getDateTime(pattern: String, milisec:Long): String {
        var sdf = SimpleDateFormat(pattern)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8:00")
        val netDate = Date(milisec)
        val datetime = sdf.format(netDate).toString()

        return datetime
    }

    fun getRecords(matric_num:String) {
        //val listener = db.collection("Student").document("s$matric_num")
        //db.collection("Student")

        db.collection("Student").document("s"+matric_num)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(ContentValues.TAG, "Current data: ${snapshot.data}")
                    val doc = snapshot.data
                    val result = doc?.get("attendance")
                    println("The attendance is: $result")
                    if (result!=null) {
                        val test = doc?.get("attendance") as ArrayList<HashMap<*,*>>
                        println("The attendance result is: $test")
                        //val ans = test as ArrayList<HashMap<*,*>>

                        val subjectArr      = ArrayList<String>()
                        val temperatureArr  = ArrayList<String>()
                        val timestampArr    = ArrayList<String>()
                        val statusArr       = ArrayList<String>()

                        test.forEach() {
                            subjectArr.add("Subject            : " + it["subject"].toString())
                            temperatureArr.add("Temperature  : " + it["temperature"].toString())
                            /*if (it["temperature"].toString() != "NULL") {
                                temperatureArr.add("Temperature  : " + it["temperature"].toString() + " °C")
                            } else {
                                temperatureArr.add("Temperature  : null")
                            }*/

                            /*if (it["timestamp"]!=null) {

                                var timestamp = it["timestamp"] as com.google.firebase.Timestamp
                                var milisec = timestamp.seconds*1000 + timestamp.nanoseconds/1000000
                                var date = getDateTime("dd/MM/yyyy", milisec)
                                var time = getDateTime("HH:mm:ss ", milisec)
                                var datetime = date +" - " + time
                                timestampArr.add("Datetime         : $datetime")

                            } else {
                                timestampArr.add("Datetime         : NULL")
                            }*/

                            timestampArr.add("Datetime         : " + it["datetime"].toString())



                            statusArr.add("Status             : " + it["status"].toString())
                        }
                        val gson            = Gson()
                        val subjectJson     = gson.toJson(subjectArr)
                        val temperatureJson = gson.toJson(temperatureArr)
                        val timestampJson   = gson.toJson(timestampArr)
                        val statusJson      = gson.toJson(statusArr)

                        session.saveRecords(subjectJson, temperatureJson, timestampJson, statusJson)

                        println("The attendance subject array is: $subjectJson")
                        println("The attendance temperature array is: $temperatureArr")
                        println("The attendance timestamp array is: $timestampArr")
                        println("The attendance status array is: $statusArr")

                    } else {

                        session.saveRecords("NULL", "NULL", "NULL", "NULL")

                    }


                    /*if (test==null) {
                        session.saveRecords(null, null, null)
                    } else {
                        val list = test as Map<*, *>
                        val attd = getLastScannedInfo(list as HashMap<String, String>)
                        val subject = scanned_val["subject"].toString()
                        val date = scanned_val["date"].toString()
                        val time = scanned_val["time"].toString()
                        session.saveLastScanned(subject, date, time)
                    }*/

                } else {
                    Log.d(ContentValues.TAG, "Current data: null")
                    Toast.makeText(context, "INVALID ACCOUNT: Please check your email", Toast.LENGTH_LONG).show()
                }
            }
    }

}