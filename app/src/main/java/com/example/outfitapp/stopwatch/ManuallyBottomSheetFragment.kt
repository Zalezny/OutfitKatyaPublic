package com.example.outfitapp.stopwatch

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.NumberPicker
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.outfitapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class ManuallyBottomSheetFragment : BottomSheetDialogFragment() {


    //variable will be useful in patch to server
    private var hourTime = 0
    private var minuteTime = 0
    private var secondTime = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_manually_bottom_sheet, container, false)

        val tietStartTimeDate : TextInputEditText = v.findViewById(R.id.tiet_start_date)
        val tietTimeWork : TextInputEditText = v.findViewById(R.id.tiet_work_time)
        val btnSaveData : Button = v.findViewById(R.id.save_data_manually_bs)
        val tilStartTimeData : TextInputLayout = v.findViewById(R.id.til_start_date)
        val tilTimeWork : TextInputLayout = v.findViewById(R.id.til_time_work)


        //default value will be today of date
        val localDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.US)
        val formatedLocalDateTime = localDateTime.format(formatter)
        tietStartTimeDate.setText(formatedLocalDateTime)

        //default value will be 00:00:00
        val defaultText = "00:00:00"
        tietTimeWork.setText(defaultText)


        tietStartTimeDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                // Opens the date picker with today's date selected
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(activity!!.supportFragmentManager, "datePicker")

            datePicker.addOnPositiveButtonClickListener {
                //take date
                val headerDateText = datePicker.headerText
                //format date
                val sdfdate = SimpleDateFormat("dd MMM yyyy").parse(headerDateText)
                val formatedDate = SimpleDateFormat("dd/MM/yyyy").format(sdfdate!!)
                //set format date


                //time Picker
                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(LocalTime.now().hour)
                    .setMinute(LocalTime.now().minute)


                    .build()
                timePicker.show(activity!!.supportFragmentManager, "tag")

                timePicker.addOnPositiveButtonClickListener{
                    val h = timePicker.hour
                    val m = timePicker.minute
                    val headerTimeText = (if (h<10) "0$h" else "$h") + ":" + (if (m<10) "0$m" else "$m")

                    //set text with chosen time
                    tietStartTimeDate.setText("$formatedDate $headerTimeText")
                }

            }
        }

        tietTimeWork.setOnClickListener {
            showWorkTimeDialog(tietTimeWork)
        }

        btnSaveData.setOnClickListener {
            if(tietStartTimeDate.text != null && tietTimeWork.text != null &&
                tietTimeWork.text.toString() != "") {
                tilTimeWork.isErrorEnabled = false
                //change string date to date date
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.US)
                val chosenDate = LocalDateTime.parse(tietStartTimeDate.text, formatter)
                //formatting date on JSON format
                val formatterJSON = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                val formattedDateJSON = chosenDate.format(formatterJSON)

                //is chosen date lesser than today?
                if(chosenDate <= LocalDateTime.now())
                {
                    tilStartTimeData.isErrorEnabled = false
                    if(tietTimeWork.text.toString() != "00:00:00")
                        {
                            tilTimeWork.isErrorEnabled = false

                            //set bundle
                            val bundleToSend = bundleOf(
                                "hour" to hourTime,
                                "minute" to minuteTime,
                                "second" to secondTime,
                                "date" to formattedDateJSON,
                            )

                            //fragment result sending to StoperFragment.kt
                            setFragmentResult("chosenDataToSend", bundleToSend)

                            dismiss()
                        }
                    else
                            tilTimeWork.error = "Nie może być zerem!"
                }
                else {
                    tilStartTimeData.error = "Data z przyszłości"
                }
            }
            else if(tietTimeWork.text.toString() == ""){
                tilTimeWork.error= "Nie może być zerem!"
            }
        }



        return v
    }

    private fun showWorkTimeDialog(etWorkTime : TextInputEditText) {
        val dialog = Dialog(activity!!)

        //disable the default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //set turn off window after click in shadow
        dialog.setCancelable(true)
        //set view
        dialog.setContentView(R.layout.dialog_picker)

        //init views from dialog_picker
        val hourPicker : NumberPicker = dialog.findViewById(R.id.hour_picker)
        val minutePicker : NumberPicker = dialog.findViewById(R.id.minute_picker)
        val secondPicker : NumberPicker = dialog.findViewById(R.id.second_picker)

        //max and minimum value of picker
        hourPicker.minValue = 0
        hourPicker.maxValue = 23

        //max and minimum value of picker
        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        //max and minimum value of picker
        secondPicker.minValue = 0
        secondPicker.maxValue = 59

        //init buttons of dialog_picker
        val btnOK : Button = dialog.findViewById(R.id.ok_btn_dialog)
        val btnCancel : Button = dialog.findViewById(R.id.cancel_btn_dialog)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnOK.setOnClickListener {
            val h = hourPicker.value
            val m = minutePicker.value
            val s = secondPicker.value


            hourTime = h
            minuteTime = m
            secondTime = s

//            val time = (if (h<10) "0$h" else "$h") + ":" + (if (m<10) "0$m" else "$m") +
//                    ":" + (if (s<10) "0$s" else "$s")
//            val timeString = (if (h>4) "$h godzin " else if(h==1) "$h godzina " else if (h in 2..4) "$h godziny " else "") +
//                    (if (m!=0) "$m minut " else "") +
//                    (if (s!=0) "$s sekund" else "")

            //String wihich will show for user in edit text
            val timeString = (if (h!=0) "$h h " else "") + (if (m!=0) "$m m " else "") +
                     (if (s!=0) "$s s " else "")

            etWorkTime.setText(timeString)
            dialog.dismiss()
        }

        dialog.create()
        dialog.show()
    }

}