package com.masoud.persiandatetimepicker.datepicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.masoud.persiandatetimepicker.R
import com.masoud.persiandatetimepicker.datepicker.UtcDates.canonicalYearMonthDay

/**
 * Represents the days of a particular month with TextView for each day.
 */
internal class MonthAdapter(
  val month: Month,
  @JvmField val dateSelector: DateSelector<*>,
  val calendarConstraints: CalendarConstraints,
  val dayViewDecorator: DayViewDecorator?
) : BaseAdapter() {
    private var previouslySelectedDates: MutableCollection<Long>
    @JvmField
    var calendarStyle: CalendarStyle? = null

    init {
        this.previouslySelectedDates = dateSelector.selectedDays
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getItem(position: Int): Long? {
        if (position < firstPositionInMonth() || position > lastPositionInMonth()) {
            return null
        }
        val day = positionToDay(position)
        return month.getDay(day)
    }

    override fun getItemId(position: Int): Long {
        return (position / month.daysInWeek).toLong()
    }

    override fun getCount(): Int {
        // Enough cells for up to 6 weeks with some offset
        // old code had up to dayOfMonth + dayOfWeek - 1
        return 42 // 6 rows * 7 columns
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        initializeStyles(parent.context)
        val dayTextView =
            (convertView
                ?: LayoutInflater.from(parent.context)
                    .inflate(R.layout.calendar_day, parent, false)) as TextView

        val offset = position - firstPositionInMonth()
        var dayNumber: Int = NO_DAY_NUMBER
        if (offset < 0 || offset >= month.daysInMonth) {
            dayTextView.visibility = View.GONE
            dayTextView.isEnabled = false
        } else {
            dayNumber = offset + 1
            dayTextView.tag = month
            dayTextView.text = dayNumber.toString()
            dayTextView.visibility = View.VISIBLE
            dayTextView.isEnabled = true
        }

        val date = getItem(position)
        if (date == null) {
            return dayTextView
        }
        updateSelectedState(dayTextView, date, dayNumber)
        return dayTextView
    }

    fun updateSelectedStates(monthGrid: MaterialCalendarGridView) {
        // Update previously selected
        for (date in previouslySelectedDates) {
            updateSelectedStateForDate(monthGrid, date)
        }
        // Update current
        for (date in dateSelector.selectedDays) {
            updateSelectedStateForDate(monthGrid, date)
        }
        previouslySelectedDates = dateSelector.selectedDays
    }

    private fun updateSelectedStateForDate(monthGrid: MaterialCalendarGridView, date: Long) {
        if (Month.create(date) == month) {
            val day = month.getDayOfMonth(date)
            val position = dayToPosition(day)
            val child = monthGrid.getChildAt(position - monthGrid.firstVisiblePosition)
            if (child is TextView) {
                updateSelectedState(child, date, day)
            }
        }
    }

    private fun updateSelectedState(dayTextView: TextView?, date: Long, dayNumber: Int) {
        if (dayTextView == null) {
            return
        }
        val context = dayTextView.context
        val contentDescription = DateStrings.getDayContentDescription(
            context, date,
            isToday(date),
            isStartOfRange(date),
            isEndOfRange(date)
        )
        dayTextView.contentDescription = contentDescription

        val style: CalendarItemStyle
        val valid = calendarConstraints.dateValidator.isValid(date)
        var selected = false
        if (valid) {
            dayTextView.isEnabled = true
            selected = isSelected(date)
            dayTextView.isSelected = selected
            style = if (selected) {
                calendarStyle!!.selectedDay
            } else if (isToday(date)) {
                calendarStyle!!.todayDay
            } else {
                calendarStyle!!.day
            }
        } else {
            dayTextView.isEnabled = false
            style = calendarStyle!!.invalidDay
        }

        if (dayViewDecorator != null && dayNumber != NO_DAY_NUMBER) {
            val y = month.year
            val m = month.month
            // allow custom
            style.styleItem(
                dayTextView,
                dayViewDecorator.getBackgroundColor(context, y, m, dayNumber, valid, selected),
                dayViewDecorator.getTextColor(context, y, m, dayNumber, valid, selected)
            )

            val drawableLeft =
                dayViewDecorator.getCompoundDrawableLeft(context, y, m, dayNumber, valid, selected)
            val drawableTop =
                dayViewDecorator.getCompoundDrawableTop(context, y, m, dayNumber, valid, selected)
            val drawableRight =
                dayViewDecorator.getCompoundDrawableRight(context, y, m, dayNumber, valid, selected)
            val drawableBottom = dayViewDecorator.getCompoundDrawableBottom(
                context,
                y,
                m,
                dayNumber,
                valid,
                selected
            )
            dayTextView.setCompoundDrawables(
                drawableLeft,
                drawableTop,
                drawableRight,
                drawableBottom
            )

            val decoratorContentDescription =
                dayViewDecorator.getContentDescription(
                    context,
                    y,
                    m,
                    dayNumber,
                    valid,
                    selected,
                    contentDescription
                )
            dayTextView.contentDescription = decoratorContentDescription
        } else {
            style.styleItem(dayTextView)
        }
    }

    private fun isToday(date: Long): Boolean {
        return canonicalYearMonthDay(date) == UtcDates.todayCanonical
    }

    private fun isStartOfRange(date: Long): Boolean {
        for (range in dateSelector.selectedRanges) {
            if (range.first != null && range.first == date) {
                return true
            }
        }
        return false
    }

    private fun isEndOfRange(date: Long): Boolean {
        for (range in dateSelector.selectedRanges) {
            if (range.second != null && range.second == date) {
                return true
            }
        }
        return false
    }

    private fun isSelected(date: Long): Boolean {
        val canonicalDate = canonicalYearMonthDay(date)
        for (selectedDay in dateSelector.selectedDays) {
            if (canonicalDate == canonicalYearMonthDay(selectedDay)) {
                return true
            }
        }
        return false
    }

    private fun initializeStyles(context: Context) {
        if (calendarStyle == null) {
            calendarStyle = CalendarStyle(context)
        }
    }

    fun firstPositionInMonth(): Int {
        return month.daysFromStartOfWeekToFirstOfMonth(calendarConstraints.firstDayOfWeek)
    }

    fun lastPositionInMonth(): Int {
        return firstPositionInMonth() + month.daysInMonth - 1
    }

    fun positionToDay(position: Int): Int {
        return position - firstPositionInMonth() + 1
    }

    fun dayToPosition(day: Int): Int {
        val offset = day - 1
        return firstPositionInMonth() + offset
    }

    fun withinMonth(position: Int): Boolean {
        return position >= firstPositionInMonth() && position <= lastPositionInMonth()
    }

    fun isFirstInRow(position: Int): Boolean {
        return (position % month.daysInWeek) == 0
    }

    fun isLastInRow(position: Int): Boolean {
        return ((position + 1) % month.daysInWeek) == 0
    }

    companion object {
        const val MAXIMUM_WEEKS: Int = 6 // in Persian we consider up to 6
        const val NO_DAY_NUMBER: Int = -1
    }
}
