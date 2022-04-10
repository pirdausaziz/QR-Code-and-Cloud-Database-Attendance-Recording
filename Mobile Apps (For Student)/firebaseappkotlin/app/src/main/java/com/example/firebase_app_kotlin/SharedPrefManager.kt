package com.example.firebase_app_kotlin

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager {

    var pref: SharedPreferences
    var edior: SharedPreferences.Editor
    var context: Context
    var PRIVATE_MODE: Int = 0

    constructor(context: Context) {

        this.context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        edior = pref.edit()
    }

    companion object {

        val PREF_NAME: String           = "LoginActivity"
        val IS_LOGIN: String            = "isLogin"
        val KEY_NAME: String            = "name"
        val KEY_ID: String              = "id"
        val KEY_SUBJECT_SCAN: String    = "subject_scan"
        val KEY_DATE_SCAN: String       = "date_scan"
        val KEY_TIME_SCAN: String       = "time_scan"
        val KEY_TEMP_SCAN: String       = "temp_scan"
        val ATTD_SUBJECT: String        = "attd_subject"
        val ATTD_DATETIME: String       = "attd_datetime"
        val ATTD_TEMPERATURE: String    = "attd_temperature"
        val ATTD_STATUS: String         = "attd_status"
    }

    fun createLoginSession(name: String, id: String) {

        edior.putBoolean(IS_LOGIN, true)
        edior.putString(KEY_NAME, name)
        edior.putString(KEY_ID, id)
        edior.commit()
        //Toast.makeText(this,"SUCCESSFUL LOGGED IN!",Toast.LENGTH_SHORT).show()
    }
    fun getLastScanned(): HashMap<String, String> {

        var lastScanned: Map<String, String> = HashMap<String, String>()
        (lastScanned as HashMap).put(KEY_SUBJECT_SCAN, pref.getString(KEY_SUBJECT_SCAN, null).toString())
        lastScanned.put(KEY_DATE_SCAN, pref.getString(KEY_DATE_SCAN, "NONE").toString())
        lastScanned.put(KEY_TIME_SCAN, pref.getString(KEY_TIME_SCAN, "NONE").toString())
        lastScanned.put(KEY_TEMP_SCAN, pref.getString(KEY_TEMP_SCAN, "NONE").toString())

        return lastScanned
    }

    fun getRecords(): HashMap<String, String> {
        var record_list: HashMap<String, String> = HashMap<String, String>()
        (record_list as HashMap).put(ATTD_SUBJECT, pref.getString(ATTD_SUBJECT, null).toString())
        record_list.put(ATTD_DATETIME, pref.getString(ATTD_DATETIME, null).toString())
        record_list.put(ATTD_TEMPERATURE, pref.getString(ATTD_TEMPERATURE, null).toString())
        record_list.put(ATTD_STATUS, pref.getString(ATTD_STATUS, null).toString())

        return record_list
    }

    fun saveRecords(subject: String?, temperature: String?, datetime: String?, status: String?) {

        edior.putString(ATTD_SUBJECT, subject)
        edior.putString(ATTD_TEMPERATURE, temperature)
        edior.putString(ATTD_DATETIME, datetime)
        edior.putString(ATTD_STATUS, status)
        edior.commit()
    }

    fun saveLastScanned(subject: String, date: String, time: String, temperature: String) {
        edior.putString(KEY_SUBJECT_SCAN, subject)
        edior.putString(KEY_DATE_SCAN, date)
        edior.putString(KEY_TIME_SCAN, time)
        edior.putString(KEY_TEMP_SCAN, temperature)
        edior.commit()
        //Toast.makeText(context, "Successfully saved", Toast.LENGTH_SHORT).show()
    }


    fun checkLogin(): Boolean {

        return this.isLoggedIn()
    }

    fun getUserDetails(): HashMap<String, String> {

        var user: Map<String, String> = HashMap<String, String>()
        (user as HashMap).put(KEY_NAME, pref.getString(KEY_NAME, null).toString())
        user.put(KEY_ID, pref.getString(KEY_ID, null).toString())


        return user
    }

    fun LogoutUser() {
        edior.clear()
        edior.apply()
        //edior.commit()

        /*val i: Intent = Intent(context, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(i)*/
    }

    fun isLoggedIn(): Boolean {

        return pref.getBoolean(IS_LOGIN, false)
    }
}

