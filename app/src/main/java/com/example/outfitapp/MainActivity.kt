// @author Zalezny
package com.example.outfitapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.outfitapp.application.OutfitApplication
import com.example.outfitapp.recyclers.OutfitAdapter
import com.example.outfitapp.fragments.BottomSheetFragment
import com.example.outfitapp.models.OutfitDataModel
import com.example.outfitapp.roomdatabase.AppDatabase
import com.example.outfitapp.roomdatabase.Outfit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.IOException
import okhttp3.*
import org.json.JSONArray


class MainActivity : AppCompatActivity() {

    private lateinit var outfitRecyclerView: RecyclerView
    private lateinit var outfitArrayList: ArrayList<OutfitDataModel>
    private lateinit var outfitAdapter : OutfitAdapter
    private lateinit var outfitProgressBar : ProgressBar
    private lateinit var outfitSwipeRefreshLayout : SwipeRefreshLayout

    // is it visible delete?
    private var isVix = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        outfitProgressBar = findViewById(R.id.outfit_progress_bar)



        //Take data from bottom sheet and adding it to recycler view
        supportFragmentManager
            .setFragmentResultListener("BottomSheetFragmentRequest", this) { _, bundle ->
                //take result from bottom sheet
                val resultOutfitName : String = bundle.getString("outfitName")!!
                val resultMainID : String = bundle.getInt("mainID")!!.toString()
                val createdData = OutfitDataModel(resultOutfitName, resultMainID, false)
                //Add item to recycler View
                outfitAdapter.addItem(createdData)
            }

        //refresh layout
        outfitSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_outfit)
        outfitSwipeRefreshLayout.setOnRefreshListener {
            //Delayed
            Handler().postDelayed(Runnable {
                outfitSwipeRefreshLayout.isRefreshing = false
                getOutfitNameData()
            }, 1000)


        }

        //Bottom Sheet for add outfit action
        val bottomSheetFragment = BottomSheetFragment()
        val fabAdd : FloatingActionButton = findViewById(R.id.addFAButton)
        fabAdd.setOnClickListener {
            bottomSheetFragment.show(supportFragmentManager, "BottomSheetDialog")
        }

        getOutfitNameData()


    }

    override fun onRestart() {
        super.onRestart()
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_time_activity_file_key),
            Context.MODE_PRIVATE
        ) ?: return
        val count = sharedPref.getInt(getString(R.string.preference_time_activity_countInTab_key), -1)
        val isEnded = sharedPref.getBoolean(getString(R.string.preference_time_activity_isEnded_key), false)
        if(count >= 0 && isEnded)
        {
            println("Prefs is $count and $isEnded")
            //delete from database and change in recycler View
            outfitAdapter.changeShadowItem(count)
            //clear shared Pref
            sharedPref.edit().clear().commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.outfit_menu, menu)
        return true
    }

    //menu selected

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.delete_item_outfit -> {
                //when icon is gone
                isVix = if(!isVix) {
                    outfitAdapter.showIcon(true)
                    true
                }
                //when icon is visible
                else {
                    outfitAdapter.showIcon(false)
                    false
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun getOutfitNameData()  {

        outfitProgressBar.visibility = View.VISIBLE

        outfitRecyclerView = findViewById(R.id.recyclerView)
        outfitRecyclerView.layoutManager =
            LinearLayoutManager(this)
        outfitRecyclerView.setHasFixedSize(true)

        outfitArrayList = arrayListOf()

        /** FOR APP WITH ROOM DATABASE **/

        val outfitDao = (application as OutfitApplication).repository

        lifecycle.coroutineScope.launch {
            val outfits : List<Outfit> = outfitDao.getOutfits()
            //convert Outfit to OutfitDataModel
            for(item in outfits.reversed())
            {
                val data = OutfitDataModel(item.outfitName!!, item.oid.toString(), item.isEnded!!)
                outfitArrayList.add(data)
            }
            runOnUiThread {
                outfitAdapter = OutfitAdapter(outfitArrayList, application)
                outfitRecyclerView.adapter = outfitAdapter

                outfitProgressBar.visibility = View.GONE
            }
        }




        /** FOR REALLY APP WITH ONLINE DATABASE **/

       /* //Use library okHttpClient to take body from JSON
        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .url(ConstDatabase.OUTFIT_URL)
            .addHeader("authorization", ConstDatabase.OUTFIT_KEY)
            .build()

        //Function is ansych, so we have to predicted this
        okHttpClient.newCall(request).enqueue(object : Callback {
            //when will be Fail
            override fun onFailure(call: Call, e: IOException) {
                Log.d("getOrdersData", "Response: $e")

                runOnUiThread {
                        outfitProgressBar.visibility = View.GONE
                        Snackbar.make(outfitSwipeRefreshLayout, "Brak połączenia", Snackbar.LENGTH_INDEFINITE)
                            .setAction("PONÓW") {
                                getOutfitNameData()
                            }
                            .show()
                }

            }

            //when will be Response, function asynch!!!
            override fun onResponse(call: Call, response: Response) {

                response.apply {

                    if(!response.isSuccessful) throw IOException("Unexpected code $response")
                    val body = response.body!!.string()

                    val outfitArrayList = arrayListOf<OutfitDataModel>()

                    //first Array from Json
                    val outfitArrayJSON = JSONArray(body)

                    for(i in outfitArrayJSON.length()-1 downTo 0)
                    {
                        val title = outfitArrayJSON.getJSONObject(i).getString("title")
                        //for addBottomSheetFragment
                        val mainId = outfitArrayJSON.getJSONObject(i).getString("_id")

                        val isEnded = outfitArrayJSON.getJSONObject(i).getBoolean("ended")

                        val data = OutfitDataModel(title, mainId, isEnded)
                        outfitArrayList.add(data)

                    }



                    //because it is asynch function and view cannot be here, we take this to new ui thread

                    runOnUiThread {
                        outfitAdapter = OutfitAdapter(outfitArrayList)
                        outfitRecyclerView.adapter = outfitAdapter

                        outfitProgressBar.visibility = View.GONE

                    }

                }
            }


        })*/


    }


}