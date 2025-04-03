package com.ms.persiandatetimepicker

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.masoud.persiandatetimepicker.datepicker.MaterialDatePicker
import com.masoud.persiandatetimepicker.timepicker.TimePickerBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btn_date_picker).setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("انتخاب تاریخ")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setEnableHoliday(true)
                    //.setDayViewDecorator(HolidayDecorator()) // or other decorator
                    .setPositiveButtonText(R.string.confirm)
                    .setNegativeButtonText(R.string.cancel)
                    .build()
            datePicker.show(supportFragmentManager,datePicker.toString())

            /*datePicker.addOnPositiveButtonClickListener { selectedDate ->
                // selectedDate is Long
            }*/
        }

        findViewById<Button>(R.id.btn_date_range_picker).setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("انتخاب محدوده تاریخ")
                    .setEnableHoliday(true)
                    .setPositiveButtonText(R.string.confirm)
                    .build()
            datePicker.show(supportFragmentManager,datePicker.toString())

            /*datePicker.addOnPositiveButtonClickListener { selection ->
                val startDate = selection.first // startDate is Long
                val endDate = selection.second // endDate is Long
            }*/
        }

        findViewById<Button>(R.id.btn_time_picker).setOnClickListener {
            val timePicker = TimePickerBuilder(this)
                // .setInitialTime(10, 30)
                //.setTitle("انتخاب زمان")
                .setOnConfirmListener { selectedTime ->
                    Toast.makeText(this, "Selected Time: $selectedTime", Toast.LENGTH_SHORT).show()
                }
            timePicker.show()
        }
    }
}