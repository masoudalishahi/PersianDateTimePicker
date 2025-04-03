package com.masoud.persiandatetimepicker.datepicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.masoud.persiandatetimepicker.R

internal class YearGridAdapter(private val materialCalendar: MaterialCalendar<*>) :
    RecyclerView.Adapter<YearGridAdapter.ViewHolder?>() {
    internal class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(
        textView
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val yearView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_year, parent, false) as TextView
        return ViewHolder(yearView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val year = getYearForPosition(position)
        holder.textView.text = year.toString()
        holder.textView.contentDescription =
            DateStrings.getYearContentDescription(holder.textView.context, year)

        val styles = materialCalendar.calendarStyle
        val today = UtcDates.todayCalendar
        val todayYear = today.get(PersianCalendarHelper.YEAR)
        var style = if (todayYear == year) styles.todayYear else styles.year

        for (day in materialCalendar.dateSelector!!.selectedDays) {
            val c = PersianCalendarHelper(day)
            val selectedYear = c.get(PersianCalendarHelper.YEAR)
            if (selectedYear == year) {
                style = styles.selectedYear
            }
        }
        style.styleItem(holder.textView)
        holder.textView.setOnClickListener(View.OnClickListener { v: View? ->
            val current = Month.create(year, materialCalendar.currentMonth!!.month)
            val cc = materialCalendar.calendarConstraints
            val moveTo = cc!!.clamp(current)
            materialCalendar.setCurrentMonth(moveTo)
            materialCalendar.setSelector(MaterialCalendar.CalendarSelector.DAY)
        })
    }

    override fun getItemCount(): Int {
        return materialCalendar.calendarConstraints!!.yearSpan
    }

    fun getPositionForYear(year: Int): Int {
        return year - materialCalendar.calendarConstraints!!.start.year
    }

    fun getYearForPosition(position: Int): Int {
        return materialCalendar.calendarConstraints!!.start.year + position
    }
}
