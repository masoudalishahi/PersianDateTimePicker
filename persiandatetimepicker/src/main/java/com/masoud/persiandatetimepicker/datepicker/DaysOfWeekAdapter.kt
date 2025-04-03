package com.masoud.persiandatetimepicker.datepicker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.masoud.persiandatetimepicker.R

/**
 * A row adapter representing the days of the week for Persian Calendar.
 */
internal class DaysOfWeekAdapter : BaseAdapter {
    private val daysInWeek = 7
    private val firstDayOfWeek: Int

    // Default constructor: assumes Saturday as the first day of week
    constructor() {
        this.firstDayOfWeek = 1
    }

    // Constructor with custom first day of week
    constructor(firstDayOfWeek: Int) {
        this.firstDayOfWeek = firstDayOfWeek
    }

    // Total number of week days
    override fun getCount(): Int {
        return daysInWeek
    }

    // Returns the calendar constant for a given position
    override fun getItem(position: Int): Int? {
        if (position >= daysInWeek) return null
        return positionToDayOfWeek(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    // Creates the view for each day of week cell
    @SuppressLint("WrongConstant")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var tv = convertView as TextView?
        if (tv == null) {
            tv = LayoutInflater.from(parent.context)
                .inflate(R.layout.calendar_day_of_week, parent, false) as TextView?
        }

        val dayOfWeek = positionToDayOfWeek(position)

        // Persian short day names: Saturday to Friday
        val shortNames = arrayOf<String?>("ش", "ی", "د", "س", "چ", "پ", "ج")
        var index = dayOfWeek - 1
        if (index < 0) index = 0
        if (index > 6) index = 6

        tv!!.text = shortNames[index]
        tv.setContentDescription("DayOfWeek " + shortNames[index])

        return tv
    }

    // Maps adapter position to Persian calendar day constant
    private fun positionToDayOfWeek(position: Int): Int {
        var dayConstant = position + firstDayOfWeek
        if (dayConstant > daysInWeek) {
            dayConstant = dayConstant - daysInWeek
        }
        return dayConstant
    }
}
