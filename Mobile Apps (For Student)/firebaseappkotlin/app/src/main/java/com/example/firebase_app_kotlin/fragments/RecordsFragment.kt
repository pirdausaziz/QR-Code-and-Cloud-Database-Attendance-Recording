package com.example.firebase_app_kotlin.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_app_kotlin.FirebaseClass
import com.example.firebase_app_kotlin.R
import com.example.firebase_app_kotlin.RecyclerAdapter
import com.example.firebase_app_kotlin.SharedPrefManager


class RecordsFragment : Fragment() {

    //private var layoutManager: RecyclerView.LayoutManager? = null
    //private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    lateinit var db: FirebaseClass
    lateinit var session: SharedPrefManager



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseClass(requireActivity().applicationContext)
        session = SharedPrefManager(requireActivity().applicationContext)

        session.getUserDetails()["id"]?.let { db.getRecords(it) }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = RecyclerAdapter(SharedPrefManager(requireActivity().applicationContext).getRecords())
        recyclerView.adapter = adapter
        /*recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = RecyclerAdapter()
        }*/

    }

}