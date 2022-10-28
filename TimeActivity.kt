package com.example.outfitapp
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.outfitapp.adapters.TimePagerAdapter


class TimeActivity : AppCompatActivity() {

    private var countInTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        //GET DATA
        val bundle : Bundle? = intent.extras
        val titleIntent = bundle!!.getString("title")
        countInTab = bundle.getInt("countInTab")

        //ACTION BAR
        val actionBar = supportActionBar
        //set title of actionBar
        actionBar!!.title = titleIntent

        //show on action bar the back arrow
        actionBar.setDisplayHomeAsUpEnabled(true)



        val viewPager2 : ViewPager2 = findViewById(R.id.vp2_time)

        val pagerAdapter = TimePagerAdapter(this)
        viewPager2.adapter = pagerAdapter
        viewPager2.currentItem = 0

        viewPager2.reduceDragSensitivity()
    }

    //back arrow
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.time_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.end_item_time -> {
                // when end is clicked:
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("Czy chcesz zakończyć?")
            .setMessage("Twój strój zostanie dodany do zakończonych")
            .setNegativeButton("NIE", null)
            .setPositiveButton("TAK") { _, _ ->
                val intent = Intent()

                val sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_time_activity_file_key),
                    Context.MODE_PRIVATE
                )
                with(sharedPref.edit()){
                    putBoolean(getString(R.string.preference_time_activity_isEnded_key), true)
                    putInt(getString(R.string.preference_time_activity_countInTab_key), countInTab)
                    apply()
                }
                finish()
            }.create().show()
    }




    //EXTENSIONS to reduce the sensitivity of ViewPager2, help with scrolling problems
    //src: https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414
    private fun ViewPager2.reduceDragSensitivity(f: Int = 5) {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop*f)       // "8" was obtained experimentally
    }



}