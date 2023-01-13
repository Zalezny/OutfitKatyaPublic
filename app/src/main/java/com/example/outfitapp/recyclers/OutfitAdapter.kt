package com.example.outfitapp.recyclers

import android.app.AlertDialog
import android.content.Intent
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.outfitapp.R
import com.example.outfitapp.TimeActivity
import com.example.outfitapp.models.OutfitDataModel
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException



class OutfitAdapter(private var outfitList : ArrayList<OutfitDataModel>) : RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder>() {

    private var isVisible = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.outfit_item,
            parent, false)
        return OutfitViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutfitViewHolder, position: Int) {
        val currentItem = outfitList[position]
        val icDelete = holder.icDelete

        // when outfit is ended
        val isEnded = currentItem.isEnded
        if(isEnded)
        {
            holder.llOutfitName.alpha = 0.3F
        }
        else
        {
            holder.llOutfitName.alpha = 1F
        }

        holder.tvOutfitName.text = currentItem.outfitName

        holder.llOutfitName.setOnClickListener { v ->
            val intent = Intent(v.context, TimeActivity::class.java)
            intent.apply {
                putExtra("title", currentItem.outfitName)
                //for addBottomSheetFragment
                putExtra("chosenOutfitID", currentItem.mainId)
                putExtra("countInTab", position)
            }
                v.context.startActivity(intent)


        }

        //Resigned to icDelete
        if(isVisible)
            icDelete.visibility = View.VISIBLE
        else
            icDelete.visibility = View.GONE

        //SET ON CLICK LISTENER of icDelete
        icDelete.setOnClickListener { v ->


            //Alert Dialog to will be sure that user want delete it
            AlertDialog.Builder(v.context)
                .setTitle("Usuń ${currentItem.outfitName}")
                .setMessage("Czy napewno chcesz go usunąć?")
                .setNegativeButton("NIE", null)
                .setPositiveButton("TAK") { p0, p1 ->

                    //remove item from list
                    outfitList.remove(currentItem)
                    //it inform adapter about delete it
                    notifyItemRemoved(position)
                    //data list is refreshes
                    notifyDataSetChanged()
                    //send request about delete it from base (above is only local)
//                    requestDatabase(currentItem.mainId!!, DELETE_OUTFIT_DATABASE_KEY)


                }.create().show()



        }
    }

    override fun getItemCount(): Int {
        return outfitList.size
    }


    class OutfitViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val tvOutfitName : TextView = itemView.findViewById(R.id.tvOutfitName)
        val llOutfitName : LinearLayout = itemView.findViewById(R.id.outfit_name_linear_layout)
        val icDelete : ImageView = itemView.findViewById(R.id.ic_delete_outfit)
    }

    //Adding new item to recycler view
    fun addItem(item : OutfitDataModel){
        outfitList.add(0, item)
        notifyDataSetChanged()
    }

    //Change boolean for visible of delete icon
    fun showIcon(vix : Boolean) {
        isVisible = vix
        notifyDataSetChanged()
    }

    fun changeShadowItem(pos: Int) {
        val oldItem = outfitList[pos]
        val newItem = OutfitDataModel(oldItem.outfitName, oldItem.mainId, true)
        outfitList.removeAt(pos)
        outfitList.add(pos, newItem)
        notifyItemChanged(pos)
/*        requestDatabase(newItem.mainId!!, CHANGE_ENDED_OUTFIT_DATABASE_KEY)*/
    }


/*    private fun requestDatabase(idOutfit: String, action: String) {
        val dynamicUrl = "${ConstDatabase.OUTFIT_URL}$idOutfit"

        val okHttpClient = OkHttpClient()

        if(action == DELETE_OUTFIT_DATABASE_KEY) {
            val request = Request.Builder()
                .url(dynamicUrl)
                .addHeader("authorization", ConstDatabase.OUTFIT_KEY)
                .delete()
                .build()

            Log.d("SToperFragmet", dynamicUrl)

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(javaClass.name, "$e")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d(javaClass.name, "Request delete Successful")
                    }
                }

            })
        }
        if(action == CHANGE_ENDED_OUTFIT_DATABASE_KEY) {
            val json = JsonObject()
            json.addProperty("ended", true)

            val request = Request.Builder()
                .patch(json.toString().toRequestBody("application/json".toMediaType()))
                .url(dynamicUrl)
                .addHeader("authorization", ConstDatabase.OUTFIT_KEY)
                .build()

            okHttpClient.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(javaClass.name, "$e")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d(javaClass.name, "Request has changed ended Successful")

                    }
                }

            })
        }
    }*/

    companion object {
        private const val DELETE_OUTFIT_DATABASE_KEY = "delete_outfit_database_key"
        private const val CHANGE_ENDED_OUTFIT_DATABASE_KEY = "change_ended_outfit_database_key"
    }



}

