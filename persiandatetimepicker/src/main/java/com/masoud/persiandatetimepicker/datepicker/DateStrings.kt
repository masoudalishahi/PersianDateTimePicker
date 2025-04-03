package com.masoud.persiandatetimepicker.datepicker

import android.content.Context
import androidx.core.util.Pair
import com.masoud.persiandatetimepicker.R
import saman.zamani.persiandate.PersianDate
import java.text.SimpleDateFormat
import java.util.Date

internal object DateStrings {
    @JvmStatic
    fun getYearMonth(timeInMillis: Long): String {
        val pd = PersianDate(timeInMillis)
        return pd.monthName() + " " + pd.shYear
    }

    fun getYearMonthDay(timeInMillis: Long): String {
        val pd = PersianDate(timeInMillis)
        return pd.shDay.toString() + " " + pd.monthName() + " " + pd.shYear
    }

    fun getMonthDay(timeInMillis: Long): String {
        val pd = PersianDate(timeInMillis)
        return pd.shDay.toString() + " " + pd.monthName()
    }

    fun getMonthDayOfWeekDay(timeInMillis: Long): String {
        val pd = PersianDate(timeInMillis)
        val dayName = pd.dayName()
        return dayName + " " + pd.shDay + " " + pd.monthName()
    }

    fun getYearMonthDayOfWeekDay(timeInMillis: Long): String {
        val pd = PersianDate(timeInMillis)
        val dayName = pd.dayName()
        return dayName + " " + pd.shDay + " " + pd.monthName() + " " + pd.shYear
    }

    fun getOptionalYearMonthDayOfWeekDay(timeInMillis: Long): String {
        return getYearMonthDayOfWeekDay(timeInMillis)
    }

    @JvmStatic
    fun getDateString(timeInMillis: Long): String {
        return getDateString(timeInMillis, null)
    }

    fun getDateString(timeInMillis: Long, userDefinedDateFormat: SimpleDateFormat?): String {
        if (userDefinedDateFormat != null) {
            return userDefinedDateFormat.format(Date(timeInMillis))
        }
        return getYearMonthDay(timeInMillis)
    }

    fun getDateRangeString(start: Long?, end: Long?): Pair<String?, String?> {
        return getDateRangeString(start, end, null)
    }

    fun getDateRangeString(
        start: Long?, end: Long?, userDefinedFormat: SimpleDateFormat?
    ): Pair<String?, String?> {
        if (start == null && end == null) {
            return Pair.create<String?, String?>(null, null)
        } else if (start == null) {
            return Pair.create<String?, String?>(
                null,
                getDateString(end!!, userDefinedFormat)
            )
        } else if (end == null) {
            return Pair.create<String?, String?>(getDateString(start, userDefinedFormat), null)
        }
        val startStr = getDateString(start, userDefinedFormat)
        val endStr = getDateString(end, userDefinedFormat)
        return Pair.create<String?, String?>(startStr, endStr)
    }

    fun getDayContentDescription(
        context: Context,
        dayInMillis: Long,
        isToday: Boolean,
        isStartOfRange: Boolean,
        isEndOfRange: Boolean
    ): String {
        var base = getOptionalYearMonthDayOfWeekDay(dayInMillis)
        if (isToday) {
            base = String.format(context.getString(R.string.mtrl_picker_today_description), base)
        }
        if (isStartOfRange) {
            return String.format(
                context.getString(R.string.mtrl_picker_start_date_description),
                base
            )
        } else if (isEndOfRange) {
            return String.format(context.getString(R.string.mtrl_picker_end_date_description), base)
        }
        return base
    }

    fun getYearContentDescription(context: Context, year: Int): String {
        return context.getString(R.string.mtrl_picker_navigate_to_year_description, year)
    }
}
