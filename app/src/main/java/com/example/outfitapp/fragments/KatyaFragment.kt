@file:Suppress("FunctionName")

package com.example.outfitapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.outfitapp.R
import com.example.outfitapp.TotalTimeHelper
import com.example.outfitapp.recyclers.HourAdapter
import com.example.outfitapp.models.HoursDataModel
import com.example.outfitapp.models.TimeDataModel
import com.example.outfitapp.util.TimeUtil
import com.example.outfitapp.viewmodels.TimeViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList





class KatyaFragment : Fragment() {

    private lateinit var timeArrayList : ArrayList<TimeDataModel>
    private lateinit var katyaAdapter : HourAdapter
    private lateinit var katyaRecyclerView: RecyclerView
    private lateinit var katyaProgressBar: ProgressBar
    private lateinit var katyaNestedScrollView: NestedScrollView
    private lateinit var katyaZZZImageView : ImageView
    private lateinit var tvTime : TextView
    private var finishedThread = false

    private val model: TimeViewModel by activityViewModels()
    private val totalHelper = TotalTimeHelper()

    private var outfitID = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_katya, container, false)
        katyaRecyclerView = view.findViewById(R.id.katya_recycler_view)
        katyaProgressBar = view.findViewById(R.id.katya_progress_bar)
        katyaNestedScrollView = view.findViewById(R.id.nestedScrollView_katya)
        katyaZZZImageView = view.findViewById(R.id.iv_zzz_katya)
        tvTime = view.findViewById(R.id.tv_katya_time)

        //take chosen outfit id from OutfitAdapter
        val bundle: Bundle ?= activity?.intent?.extras
        outfitID = bundle!!.getString("chosenOutfitID")!!

        katyaRecyclerView.setHasFixedSize(true)
        katyaRecyclerView.layoutManager =
            LinearLayoutManager(activity)



//        getData(outfitID)

        //ViewModel add to recycler view from Stoperfragment.kt
        model.getKatya().observe(viewLifecycleOwner, androidx.lifecycle.Observer { map ->
            if(finishedThread) {
                //data from ViewModel
                val date = map["date"]
                val time = map["time"]
                val hourID = map["hourID"]

                //final date format
                val finalDate = finalDateFormat(date!!)

                //data to send to recyclerview
                val data = TimeDataModel(finalDate, time!!, hourID!!)

                //show recycler view if is close
                showImageZZZ(false)

                val hour = TimeUtil.getHour(time)
                val minute = TimeUtil.getMinute(time)
                val second = TimeUtil.getSecond(time)

                //add times to total time
                totalHelper.addTotalTime(hour, minute, second)
                //set text view time
                tvTime.text = totalHelper.getTime()


                //add to recycler view new item
                katyaAdapter.addItem(data)


            }

        })

        return view
    }




    /*private fun getData(id : String?)  {
        //show progress bar
        katyaProgressBar.visibility = View.VISIBLE

        //Use library okHttpClient to take body from JSON
        val okHttpClient = OkHttpClient()


        timeArrayList = arrayListOf()

        val request = Request.Builder()
            .url(ConstDatabase.OUTFIT_URL)
            .addHeader("authorization", ConstDatabase.OUTFIT_KEY)
            .build()

        //Function is ansych, so we have to predicted this
        okHttpClient.newCall(request).enqueue(object : Callback {
            //when will be Fail
            override fun onFailure(call: Call, e: IOException) {
                Log.d(javaClass.name, "Response: $e")
            }

            //when will be Response, function asynch!!!
            override fun onResponse(call: Call, response: Response) {

                response.apply {
                    if(!response.isSuccessful) throw IOException("Unexpected code $response")
                    val body = response.body!!.string()

                    //gson - change body in objects/arrays
                    val gson = GsonBuilder().create()
                    val feed = gson.fromJson(body, Array<HoursDataModel>::class.java)

                    for(i in feed.indices)
                    {
                        if(feed[i]._id == id)
                            for (j in feed[i].kateHours.size-1 downTo 0)
                            {
                                val h = feed[i].kateHours[j].hour
                                val m = feed[i].kateHours[j].minute
                                val s = feed[i].kateHours[j].second

                                //date of hours
                                val dateString = feed[i].kateHours[j].date

                                val finalDate = finalDateFormat(dateString)

                                val time : String = "$h:" + (if (m<10) "0$m" else "$m") + ":" +
                                        (if (s<10) "0$s" else "$s")

                                val timeID = feed[i].kateHours[j]._id

                                totalHelper.addTotalTime(h,m,s)
                                val element = TimeDataModel(finalDate, time, timeID)

                                timeArrayList.add(element)
                            }
                    }



                    //because it is asynch function and view cannot be here, we take this to new ui thread
                    activity?.runOnUiThread {


                        katyaAdapter = HourAdapter(timeArrayList)
                        katyaRecyclerView.adapter = katyaAdapter

                        tvTime.text = totalHelper.getTime()

                        if(timeArrayList.isNotEmpty())
                            showImageZZZ(false)
                        else
                            showImageZZZ(true)

                        //give info about finished thread / first turn on fragment
                        finishedThread = true

                        //turn off progress bar
                        katyaProgressBar.visibility = View.GONE

                        //SWIPE DELETE CONFIG

                        if(ConstDatabase.IS_KATYA)
                        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                            override fun onMove(
                                recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
                            ): Boolean {
                                //when item is moved.
                                return false
                            }

                            override fun onSwiped(
                                viewHolder: RecyclerView.ViewHolder,
                                direction: Int
                            ) {
                                var isUndo = false
                                //get the position of the item at that position.
                                val pos = viewHolder.adapterPosition
                                //getting the item at particular position.
                                val deletedItem: TimeDataModel = timeArrayList[viewHolder.adapterPosition]

                                //this method is called when the item is swiped,
                                //removed item from array list.
                                timeArrayList.removeAt(viewHolder.adapterPosition)

                                //notify our item is removed from adapter.
                                katyaAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                                AlertDialog.Builder(activity!!)
                                    .setTitle("Usuń ${deletedItem.time}")
                                    .setMessage("Czy napewno chcesz go usunąć?")
                                    .setNegativeButton("NIE") { _, _ ->
                                        //adding to our list with last position
                                        timeArrayList.add(pos, deletedItem)

                                        //inform adapter class that item is added to list
                                        katyaAdapter.notifyItemInserted(pos)
                                        //is undo click?
                                        isUndo = true
                                    }
                                    .setPositiveButton("TAK") { _, _ ->


                                        //display snackbar with action (undo)
                                        Snackbar.make(katyaRecyclerView, "Usunięto " + deletedItem.time, Snackbar.LENGTH_LONG)
                                            .setAction("Cofnij")
                                            {
                                                //adding to our list with last position
                                                timeArrayList.add(pos, deletedItem)

                                                //inform adapter class that item is added to list
                                                katyaAdapter.notifyItemInserted(pos)

                                                //is undo click?
                                                isUndo = true

                                            }
                                            .addCallback(object: Snackbar.Callback() {
                                                //when is finished
                                                override fun onDismissed(
                                                    transientBottomBar: Snackbar?,
                                                    event: Int
                                                ) {
                                                    super.onDismissed(transientBottomBar, event)

                                                    if(!isUndo)
                                                    {
                                                        val t = deletedItem.time
                                                        val h = TimeUtil.getHour(t)
                                                        val m = TimeUtil.getMinute(t)
                                                        val s = TimeUtil.getSecond(t)
                                                        //remove time from tvTime
                                                        totalHelper.removeTotalTime(h, m, s)
                                                        tvTime.text = totalHelper.getTime()
                                                        deleteFromServer(deletedItem._id, outfitID, ConstDatabase.IS_KATYA)
                                                    }
//

                                                }
                                            })
                                            .show()

                                    }.create().show()


                            }
                            //adding it to our recycler view.
                        }).attachToRecyclerView(katyaRecyclerView)
                    }
                }
            }


        })

    }

    private fun deleteFromServer(timeID : String, outfitID : String, IS_KATYA : Boolean)
    {
        val dynamicUrl = "${ConstDatabase.OUTFIT_URL}$outfitID/$timeID"

        val name = if(IS_KATYA) ConstDatabase.KATYA else ConstDatabase.MOM

        val json = JsonObject()
        json.addProperty("person", name)

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(dynamicUrl)
            .addHeader("authorization", ConstDatabase.OUTFIT_KEY)
            .patch(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(javaClass.name, "$e")
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    Log.d(javaClass.name, "Request delete Successful")
                }
            }

        })
    }*/

    //check and return date today, yesterday format
    private fun finalDateFormat(dateJSON: String) : String {
        //formatters (need in format dates)
        val formatterFromJson = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val formatterClock = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US)

        //operation on dates
        val sdfDate = LocalDateTime.parse(dateJSON, formatterFromJson)
        val finalDate = TimeUtil.ddMMyyyyHHmmss(dateJSON)
        val clock = sdfDate.format(formatterClock)

        //today date
        val localTodayDate = LocalDate.now()

        //yesterday date
        val yesterdayDate = LocalDate.now().minusDays(1)

        if(sdfDate.year == localTodayDate.year
            && sdfDate.month == localTodayDate.month
            && sdfDate.dayOfMonth == localTodayDate.dayOfMonth)
            return "Dziś, $clock"

        if(sdfDate.year == yesterdayDate.year
            && sdfDate.month == yesterdayDate.month
            && sdfDate.dayOfMonth == yesterdayDate.dayOfMonth)
            return "Wczoraj, $clock"
        return finalDate
    }

    //show or hide the image of empty Array List of RV
    private fun showImageZZZ(amount: Boolean) {
        if(amount)
        {
            katyaNestedScrollView.visibility = View.GONE
            katyaZZZImageView.visibility = View.VISIBLE
        }
        else
        {
            //Nested Scroll View has RecyclerView inside so when tab is empty
            // it is not sense show it
            katyaNestedScrollView.visibility = View.VISIBLE
            //show image which tell user that it is empty
            katyaZZZImageView.visibility = View.GONE
        }

    }


}