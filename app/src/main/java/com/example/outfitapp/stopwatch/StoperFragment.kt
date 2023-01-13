package com.example.outfitapp.stopwatch

import ConnectionLiveData
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.example.outfitapp.*
import com.example.outfitapp.util.StopwatchUtil
import com.example.outfitapp.util.TimeUtil

import com.example.outfitapp.viewmodels.TimeViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException


class StoperFragment : Fragment() {

    private val timeViewModel: TimeViewModel by activityViewModels()

    private var timeToSent: String = ""
    private var isDataToSend = false

    private lateinit var cardView: CardView
    private lateinit var tvBeforeStart: TextView
    private lateinit var ivButton: ImageView
    private lateinit var btnAdd : Button
    private lateinit var tvTimer : TextView
    private lateinit var tvAfterStart : TextView
    private lateinit var sharedPref : SharedPreferences
    private lateinit var cvChange : CardView

    private var titleOutfit : String = ""
    private var mainID : String = ""

    private var isTimerRunning = false

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted: Boolean ->
        if(isGranted) {
            Log.i("Permission Notification: ", "Granted")
        } else {
            Log.i("Permission Notification: ", "Denied")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_stoper, container, false)

        //Permission to post notifications for Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionNotification()
        }



        initViews(v)
        //set mainId
        val bundle: Bundle ?= activity?.intent?.extras
        mainID = bundle!!.getString("chosenOutfitID")!!
        //take clicked titleOutfit
        titleOutfit = bundle.getString("title")!!
        Log.d("mainID", "MainID is: $mainID")

        val btnManuallyAdd : Button = v.findViewById(R.id.btn_manually_add)

        sharedPref = activity!!.getSharedPreferences(
            getString(R.string.preference_stopwatch_file_key), Context.MODE_PRIVATE)

        //check is any sharedPref from previous not connect
            val prefOutfitID = sharedPref.getString(getString(R.string.preference_stopwatch_outfit_key), null)
            val prefPerson = sharedPref.getBoolean(getString(R.string.preference_stopwatch_person_key), false)
            val prefTime = sharedPref.getString(getString(R.string.preference_stopwatch_time_key), null)
            val prefDate = sharedPref.getString(getString(R.string.preference_stopwatch_date_key), null)
            if(prefOutfitID != null && prefTime != null && prefDate != null && prefPerson == IS_KATYA)
//                repeatSend(prefOutfitID, prefPerson, prefTime, prefDate)


        btnManuallyAdd.setOnClickListener {
            val manuallyBottomSheetFragment = ManuallyBottomSheetFragment()
            manuallyBottomSheetFragment.show(activity!!.supportFragmentManager,"ManuallyBottomSheet")
        }

        //set name of user activity
        val tvUserName : TextView = v.findViewById(R.id.tv_user_name_stopwatch)
        //who use it?
        tvUserName.text = if (IS_KATYA) "KASIA" else "MAMA"


        ivButton.setOnClickListener {


            if (!isTimerRunning) {
                //send to service info about start
                sendCommandToService(ACTION_START_SERVICE)
            } else {
                showDialog()
            }
        }
        //setting  ViewModel observers
        setObservers()


        //Set fragment result listener from ManuallyBottomSheetFragment.kt
        setFragmentResultListener("chosenDataToSend") { _, bdl ->
            val hour = bdl.getInt("hour")
            val minute = bdl.getInt("minute")
            val second = bdl.getInt("second")
            val date = bdl.getString("date")

            //make time to sendData
            val time = makeTimeString(hour.toLong(),minute.toLong(),second.toLong())
            //send data to database
//            sendData(mainID, IS_KATYA, time, date!!)

        }

        return v
    }

    private fun showDialog() {
        AlertDialog.Builder(activity!!)
            .setTitle("Czy chcesz zakończyć?")
            .setMessage("Twój czas zostanie zapisany do $titleOutfit")
            .setNegativeButton("NIE", null)
            .setPositiveButton("TAK") { _, _ ->
                //info about finish timer (use in date observer)
                isDataToSend = true
                //send info to service about stop
                sendCommandToService(ACTION_STOP_SERVICE)
                //next step of finish is in date observer...
            }.create().show()
    }

    private fun setObservers() {
        //observer of stopwatchEvent (is on or off)
        StopwatchService.stopwatchEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateUi(it)
        })
        //observer of timerInMillis
        StopwatchService.timerInMillis.observe(viewLifecycleOwner) {

            // That's probably fixing the issue with randoms set 0 value instead registered value.
            // It's security for the reset time.
            if(it != 0L)
            {
                timeToSent = StopwatchUtil.getFormattedTime(it, false)
                tvTimer.text = timeToSent
            }
            else
            {
                tvTimer.text = StopwatchUtil.getFormattedTime(it,false)
            }
        }
        //observer of dateJSONFormat
        StopwatchService.dateJSONFormat.observe(viewLifecycleOwner) {
            //if will be stop click (without it, send data on start observer)
            if(isDataToSend) {
               /* sendData(
                    mainID,
                    IS_KATYA,
                    timeToSent,
                    it
                )*/
                //set data send on false
                isDataToSend = false
            }
        }
    }

    //set changes in ui
    private fun updateUi(event: StopwatchEvent) {
        when (event) {
            is StopwatchEvent.START -> {
                isTimerRunning = true
                //ui after start
                ivButton.setImageResource(R.drawable.stoper_go)
                tvBeforeStart.visibility = View.GONE
                tvAfterStart.visibility = View.VISIBLE
                tvTimer.visibility = View.VISIBLE
            }
            is StopwatchEvent.END -> {
                isTimerRunning = false

                //ui before start
                ivButton.setImageResource(R.drawable.stoper_rest)
                tvBeforeStart.visibility = View.VISIBLE
                tvTimer.visibility = View.GONE
                tvAfterStart.visibility = View.GONE
            }
        }
    }


    //communication with StopwatchService
    private fun sendCommandToService(action: String) {
        val intent = Intent(activity, StopwatchService::class.java)
        intent.putExtra("titleOutfit", titleOutfit)
        intent.putExtra("chosenID", mainID)
        activity!!.startService(intent.apply {
            this.action = action
        })
    }

    //scheme of time in "00:00:00"
    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String
    {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    //init ui
    private fun initViews(v: View)
    {
        cardView = v.findViewById(R.id.card_view_stopwatch)
        tvBeforeStart = v.findViewById(R.id.tv_before_start)
        ivButton = v.findViewById(R.id.iv_stopwatch)
        btnAdd = v.findViewById(R.id.btn_manually_add)
        tvTimer = v.findViewById(R.id.tv_timer)
        tvAfterStart = v.findViewById(R.id.tv_after_start)
        cvChange = v.findViewById(R.id.cv_change_view_stopwatch)

    }

    //refreshes recycler views in KatyaFragment or MumFragment (using ViewModel)
    private fun refreshTimeAdapter(date: String, time : String, id: String) {
        //create map
        val map = mapOf("date" to date, "time" to time, "hourID" to id)

        if(IS_KATYA)
            timeViewModel.setKatya(map)
        else {
            println("TESTUJEMYYY")
            timeViewModel.setMom(map)
        }
    }

    //send data to database
/*    private fun sendData(outfitID: String, person: Boolean, time : String, date : String) {


        val dynamicUrl = ConstDatabase.OUTFIT_URL + outfitID
        val name = if(person) ConstDatabase.KATYA else ConstDatabase.MOM
        //00:00:00
        val hourTime = "${time[0]}${time[1]}"
        val minuteTime = "${time[3]}${time[4]}"
        val secondTime = time.takeLast(2)

        Log.d("SEND DATA", "Sending data: id: $id, person: $name, hourTime: $hourTime," +
                "minuteTime: $minuteTime, secondTime: $secondTime, date: $date ")

        val json = JsonObject()
        json.addProperty("person", name) //1- Mom, 2- Katya
        json.addProperty("hour", hourTime.toInt())
        json.addProperty("minute", minuteTime.toInt())
        json.addProperty("second", secondTime.toInt())
        json.addProperty("date", date)

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .patch(json.toString().toRequestBody("application/json".toMediaType()))
            .url(dynamicUrl)
            .addHeader("authorization", ConstDatabase.OUTFIT_KEY)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FailureResponse", "$e")
                activity!!.runOnUiThread {
                    repeatSend(outfitID, person, time, date)
                }

            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    Log.d(javaClass.name, "Response is Successful")
                    activity!!.runOnUiThread {
                        Toast.makeText(activity, "Wysłano do bazy!", Toast.LENGTH_SHORT).show()
                        //take id from body message (send back from server)
                        val bodyMsg = response.body!!.string()
                        val json = JSONTokener(bodyMsg).nextValue() as JSONObject
                        val hourID = json.getString("id")
                        //refresh recycler view in Katya or Mom Fragment
                        refreshTimeAdapter(date, time, hourID)
                    }

                }
                else
                    Log.e(javaClass.name, "Request is not RECEIVED")
            }
        })
    }*/

/*    private fun repeatSend(outfitID: String, person: Boolean, time : String, date : String) {
        var isRepeat = false
        Snackbar.make(cvChange, "Nie udało się wysłać $time", Snackbar.LENGTH_INDEFINITE)
            .setAction("Powtórz") {
                sendData(outfitID, person, time, date)
                isRepeat = true
                sharedPref.edit().putString(getString(R.string.preference_stopwatch_outfit_key), null).commit()
            }.show()

        if(!isRepeat)
        {
            with(sharedPref.edit()) {
                putString(getString(R.string.preference_stopwatch_outfit_key), outfitID)
                putBoolean(getString(R.string.preference_stopwatch_person_key), person)
                putString(getString(R.string.preference_stopwatch_time_key), time)
                putString(getString(R.string.preference_stopwatch_date_key), date)
                apply()
            }

        }
    }*/

    private fun requestPermissionNotification() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                //Permission is granted
            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.POST_NOTIFICATIONS) -> {
                //Additional rationale should be displayed
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }


    }

}