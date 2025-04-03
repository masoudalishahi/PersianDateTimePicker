package com.masoud.persiandatetimepicker.datepicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.masoud.persiandatetimepicker.R

/**
 * Manages each month page in the MaterialCalendar.
 */
internal class MonthsPagerAdapter(
    context: Context,
    dateSelector: DateSelector<*>,
    cc: CalendarConstraints,
    decorator: DayViewDecorator?,
    listener: MaterialCalendar.OnDayClickListener
) : RecyclerView.Adapter<MonthsPagerAdapter.ViewHolder?>() {
    private val calendarConstraints: CalendarConstraints
    private val dateSelector: DateSelector<*>
    private val dayViewDecorator: DayViewDecorator?
    private val onDayClickListener: MaterialCalendar.OnDayClickListener
    private val itemHeight: Int

    init {
        val first = cc.start
        val last = cc.end
        val cur = cc.openAt
        require(first.compareTo(cur) <= 0) { "start cannot be after current" }
        require(cur!! <= last) { "current cannot be after end" }
        val daysHeight = MonthAdapter.MAXIMUM_WEEKS * MaterialCalendar.getDayHeight(context)
        val labelHeight =
            (if (MaterialDatePicker.isFullscreen(context)) MaterialCalendar.getDayHeight(context) else 0)
        this.itemHeight = daysHeight + labelHeight

        this.calendarConstraints = cc
        this.dateSelector = dateSelector
        this.dayViewDecorator = decorator
        this.onDayClickListener = listener
        setHasStableIds(true)
    }

    internal class ViewHolder(container: LinearLayout, showLabel: Boolean) :
        RecyclerView.ViewHolder(container) {
        val monthTitle: TextView = container.findViewById<TextView>(R.id.month_title)
        val monthGrid: MaterialCalendarGridView

        init {
            ViewCompat.setAccessibilityHeading(monthTitle, true)
            monthGrid = container.findViewById<MaterialCalendarGridView>(R.id.month_grid)
            if (!showLabel) {
                monthTitle.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val container = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_month_labeled, parent, false) as LinearLayout

        if (MaterialDatePicker.isFullscreen(parent.context)) {
            val lp = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, itemHeight
            )
            container.layoutParams = lp
            return ViewHolder(container, true)
        } else {
            return ViewHolder(container, false)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val month = calendarConstraints.start.monthsLater(position)
        holder.monthTitle.text = month.getLongName()
        val gridView = holder.monthGrid.findViewById<MaterialCalendarGridView>(R.id.month_grid)

        @Suppress("SENSELESS_COMPARISON")
        if (gridView.adapter != null && month == gridView.adapter.month) {
            gridView.invalidate()
            gridView.adapter.updateSelectedStates(gridView)
        } else {
            val adapter = MonthAdapter(month, dateSelector, calendarConstraints, dayViewDecorator)
            gridView.numColumns = month.daysInWeek
            gridView.setAdapter(adapter)
        }

        gridView.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, pos: Int, id: Long ->
                val ma = gridView.adapter
                if (ma.withinMonth(pos)) {
                    val date = ma.getItem(pos)
                    onDayClickListener.onDayClick(date!!)
                }
            }
    }

    override fun getItemId(position: Int): Long {
        return calendarConstraints.start.monthsLater(position).stableId
    }

    override fun getItemCount(): Int {
        return calendarConstraints.monthSpan
    }

    fun getPageTitle(position: Int): CharSequence {
        return getPageMonth(position).getLongName()
    }

    fun getPageMonth(position: Int): Month {
        return calendarConstraints.start.monthsLater(position)
    }

    fun getPosition(month: Month): Int {
        return calendarConstraints.start.monthsUntil(month)
    }

}
