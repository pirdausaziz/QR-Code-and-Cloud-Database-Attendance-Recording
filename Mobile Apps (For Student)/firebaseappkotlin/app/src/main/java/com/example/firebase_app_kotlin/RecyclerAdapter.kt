package com.example.firebase_app_kotlin


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class RecyclerAdapter(var listItem: HashMap<*, *>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    //lateinit var

    //private var titles = arrayOf("Chapter One", "Chapter Two", "Chapter Three", "Chapter Four", "Chapter Five", "Chapter Six", "Chapter Seven", "Chapter Eight")
    //private var details = arrayOf("Item one details", "Item two details", "Item three details", "Item four details", "Item five details", "Item six details", "Item seven details", "Item eight details")
    //private var images = intArrayOf(R.drawable.android_logo, R.drawable.android_logo, R.drawable.android_logo, R.drawable.android_logo, R.drawable.android_logo, R.drawable.android_logo, R.drawable.android_logo, R.drawable.android_logo)

    /*constructor(session: SharedPrefManager) {
        this.session = session
    }*/
//    private var records = listItem
//    private var value = records.get("attd_datetime").toString()
//    private var arr = value.drop(1).dropLast(1).split(",")
    // private var gson =

    //private var gson = Gson()

    val re = Regex("[^A-Za-z0-9/|:°,. -]")
    //val answer = re.replace(answer, "")

    /*private var subject         = listItem["attd_subject"].toString().drop(1).dropLast(1).split(",")
    private var datetime        = listItem["attd_datetime"].toString().drop(1).dropLast(1).split(",")
    private var temperature     = listItem["attd_temperature"].toString().drop(1).dropLast(1).split(",")
    private var status          = listItem["attd_status"].toString().drop(1).dropLast(1).split(",")*/

    private var subject         = re.replace(listItem["attd_subject"].toString(), "").split(",")
    private var datetime        = re.replace(listItem["attd_datetime"].toString(), "").split(",")
    private var temperature     = re.replace(listItem["attd_temperature"].toString(), "").split(",")
    private var status          = re.replace(listItem["attd_status"].toString(), "").split(",")


    //private var titles = arrayOf("Chapter One", "Chapter Two")
    //private var details = arrayOf("Item one details", "Item two details")
    //private var images = intArrayOf(R.drawable.android_logo, R.drawable.android_logo, R.drawable.android_logo)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {

        //var a = session.getRecords()
        //println("The position is: $a")
//        holder.itemTitle.text = subject[position]
//        holder.itemDetail.text = datetime[position]
        //holder.itemImage.setImageResource(images[position])

        holder.subject_text.text = subject[position]
        holder.datetime_text.text = datetime[position]
        holder.temperature_text.text = temperature[position]
        holder.status_text.text = status[position]
    }

    override fun getItemCount(): Int {
        //println("The listItem is: ${arr.size}")
//        println("Subject is: ${subject}")

        return subject.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var subject_text: TextView
        var datetime_text: TextView
        var temperature_text: TextView
        var status_text: TextView


//        var itemImage: ImageView
//        var itemTitle: TextView
//        var itemDetail: TextView


        init {
            subject_text        = itemView.findViewById(R.id.subject_text)
            datetime_text       = itemView.findViewById(R.id.datetime_text)
            temperature_text    = itemView.findViewById(R.id.temperature_text)
            status_text         = itemView.findViewById(R.id.status_text)
        }
    }



}