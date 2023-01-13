

package com.example.outfitapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.coroutineScope
import androidx.room.Room
import com.example.outfitapp.R
import com.example.outfitapp.application.OutfitApplication
import com.example.outfitapp.models.OutfitDataModel
import com.example.outfitapp.recyclers.OutfitAdapter
import com.example.outfitapp.roomdatabase.AppDatabase
import com.example.outfitapp.roomdatabase.IdGeneratorHelper
import com.example.outfitapp.roomdatabase.Outfit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextInt

class BottomSheetFragment : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //here code
        val etOutfitName: EditText = view.findViewById(R.id.et_outfit_name)
        // onClick "enter" or "done" button
        etOutfitName.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {

                val outfitName = etOutfitName.text.toString()

                postToServer(outfitName)

                //clear text in edit text
                etOutfitName.text.clear()
            }
            false
        }


    }

    private fun postToServer(amount: String) {

        /** FOR APP WITH ROOM DATABASE **/
        val outfitDao = (activity!!.application as OutfitApplication).repository
        lifecycle.coroutineScope.launch {
            
            val item = Outfit(isEnded = false, outfitName = amount, oid = IdGeneratorHelper(activity!!).takeNewId())
            outfitDao.insertAll(item)

            activity!!.runOnUiThread {
                val bundle = bundleOf(
                    "outfitName" to amount,
                    "mainID" to item.oid
                )
                //set Fragment Result (invoke in MainActivity)
                setFragmentResult("BottomSheetFragmentRequest", bundle)

                //remove BottomSheetFragment
                dismiss()
            }
        }






        /** FOR REALLY APP WITH ONLINE DATABASE **/
        /*val json = JsonObject()
        json.addProperty("title", amount)

        //create time
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())

        json.addProperty("hour", currentTime)

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .url(ConstDatabase.ADD_OUTFIT_URL)
            .addHeader("authorization", ConstDatabase.OUTFIT_KEY)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                Log.d("FailureResponse", "$e")
                textMsg("Failed! Contact to Danielczyk!")
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful)
                {
                    //request message
                    val bodyMsg = response.body!!.string()

                    val jsonTok = JSONTokener(bodyMsg).nextValue() as JSONObject
                    val id = jsonTok.getString("id")



                    activity!!.runOnUiThread {
                        //set to bundle outfitName and mainID
                        val bundle = bundleOf(
                            "outfitName" to amount,
                            "mainID" to id
                        )
                        //set Fragment Result (invoke in MainActivity)
                        setFragmentResult("BottomSheetFragmentRequest", bundle)

                        //remove BottomSheetFragment
                        dismiss()
                    }
                }


            }
        })*/
    }

    fun textMsg(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

}



