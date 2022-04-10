package com.example.firebase_app_kotlin.fragments

import android.os.Bundle
import android.text.TextUtils.split
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.firebase_app_kotlin.R
import com.example.firebase_app_kotlin.SharedPrefManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ScannerFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var database: DatabaseReference
    lateinit var session: SharedPrefManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        session = SharedPrefManager(requireActivity().applicationContext)
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                //Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                //checkTimestamp()

                checkTimestamp(it.text.split(",")[0])

                /*if (!checkTimestamp()) {
                    Toast.makeText(context, "INVALID SESSION: No active session", Toast.LENGTH_LONG).show()
                } else if (!checkEnroll()) {
                    Toast.makeText(context, "INVALID CLASS: You are not enrolled in this class", Toast.LENGTH_LONG).show()
                } else if(!checkIndex(it.text.split(",")[0])) {
                    Toast.makeText(context, "INVALID QR: The QR code has been scanned", Toast.LENGTH_LONG).show()
                } else if(!checkScanned()) {
                    Toast.makeText(context, "INVALID QR: You have scanned the QR code", Toast.LENGTH_LONG).show()
                } else {
                    var stud_id = session.getUserDetails()["id"]
                    database = Firebase.database.reference
                    database.child("current_id").setValue(stud_id as String)
                    Toast.makeText(context,"SCAN SUCCESSFUL: Please scan temperature", Toast.LENGTH_LONG).show()
                }*/
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    fun checkTimestamp(scanText: String) {
        database = Firebase.database.reference
        database.child("timestamp").get().addOnSuccessListener {

            if(it.exists()) {
                val comTimestamp = it.getValue() as Long
                val currentTimestamp = System.currentTimeMillis()
                val dif = currentTimestamp - comTimestamp
                println("The Com timestamp is: $comTimestamp")
                println("The current timestamp is: $currentTimestamp")
                println("The dif is: " + dif)
                if (dif < 10000) {
                    checkEnroll(scanText)
                    //Toast.makeText(context, "Session Exist!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "INVALID SESSION: There is no active session at the moment", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(context, "Timestamp doesn't exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {

            Toast.makeText(context, "Timestamp doesn't exist", Toast.LENGTH_SHORT).show()
        }
    }


    fun checkEnroll(scanText: String) {
        database = Firebase.database.reference
        database.child("student_list").get().addOnSuccessListener {
            if(it.exists()) {
                var stud_id = session.getUserDetails()["id"]
                val stud_list = it.getValue() as ArrayList<String>
                //Toast.makeText(context, stud_list.toString(), Toast.LENGTH_SHORT).show()
                var enroll_result = stud_list.contains(stud_id.toString())
                if (enroll_result) {
                    checkIndex(scanText)
                    //Toast.makeText(context, "User enrolled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "INVALID SUBMISSION: You are not enrolled in this class", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(context, "Student List doesn't exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {

            Toast.makeText(context, "Student List doesn't exist", Toast.LENGTH_SHORT).show()
        }
    }


    fun checkIndex(scanText:String) {

        database = Firebase.database.reference
        database.child("current_index").get().addOnSuccessListener {
            if(it.exists()) {
                val index = it.value.toString()
                if ( index==scanText) {
                    checkScanned()
                    //Toast.makeText(context, "Index true", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "INVALID QR: Code already scanned or invalid", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Index doesn't exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {

            Toast.makeText(context, "Index doesn't exist", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkScanned() {

        database = Firebase.database.reference
        database.child("scanned_id").get().addOnSuccessListener {
            if(it.exists()) {
                var stud_id = session.getUserDetails()["id"]
                val stud_scanned = it.getValue() as ArrayList<String>
                var scanned_result = stud_scanned.contains(stud_id.toString())
                if (!scanned_result) {
                    var stud_id = session.getUserDetails()["id"]
                    database.child("current_id").setValue(stud_id as String)
                    Toast.makeText(context,"SCAN SUCCESSFUL: Please scan temperature", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context,"INVALID SUBMISSION: You already scanned QR code ", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Scanned ID doesn't exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {

            Toast.makeText(context, "Scanned ID doesn't exist", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}