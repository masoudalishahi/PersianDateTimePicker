@file:Suppress("unused", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.masoud.persiandatetimepicker.utils

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import com.masoud.persiandatetimepicker.datepicker.MaterialDatePicker
import com.masoud.persiandatetimepicker.timepicker.TimePickerBuilder
import com.masoud.persiandatetimepicker.utils.enums.GregorianDatePattern
import com.masoud.persiandatetimepicker.utils.enums.PersianDatePattern
import com.masoud.persiandatetimepicker.utils.enums.TimeZones
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.masoud.persiandatetimepicker.R


/**
 * This function will get a date object in gregorian calendar
 * and returns persian date in specified output format
 *
 * @param gregorianDate gregorian date object
 * @param outputFormat  persian date format
 * @return persian date in specified output format
 */
fun getPersianDateTime(
    gregorianDate: Date,
    outputFormat: PersianDatePattern = PersianDatePattern.PATTERN_1
): String {
    val persianDate = PersianDate(gregorianDate)
    val persianDateFormat = PersianDateFormat(outputFormat.text)

    return persianDateFormat.format(persianDate)
}

/**
 * This function will get a string date in gregorian calendar and its format
 * and returns persian date in specified output format
 *
 * @param gregorianDate gregorian date string
 * @param inputFormat   format of gregorianDate
 * @param outputFormat  persian date format
 * @param timeZone      time zone
 * @return persian date in specified output format
 */
fun getPersianDateTime(
    gregorianDate: String,
    inputFormat: String,
    outputFormat: String,
    timeZone: TimeZones = TimeZones.UTC
): String? {
    val sdf = SimpleDateFormat(inputFormat, Locale.ENGLISH)
    sdf.timeZone = timeZone.timeZone

    try {
        val date = sdf.parse(gregorianDate)
        val persianDate = PersianDate(date)
        val persianDateFormat = PersianDateFormat(outputFormat)

        return persianDateFormat.format(persianDate)

    } catch (ignored: Exception) {
    }

    return null
}

/**
 * Converts a given Gregorian date string to a Persian date string, preserving the time component.
 *
 * This function takes a Gregorian date string in a specific format, extracts the time component,
 * converts the date part to a Persian date, and then returns a string that combines both the Persian
 * date and the time component.
 *
 * @param gregorianDate The input Gregorian date string in the format "yyyy-MM-dd HH:mm:ss".
 * @return A string representing the corresponding Persian date and the original time component.
 */
@SuppressLint("SimpleDateFormat")
fun getPersianDateWithGregorianTime(gregorianDate: String): String {
    val inputFormat = SimpleDateFormat(GregorianDatePattern.PATTERN_1.text, Locale.ENGLISH)
    val timeFormat = SimpleDateFormat(GregorianDatePattern.PATTERN_3.text, Locale.ENGLISH)

    val date = inputFormat.parse(gregorianDate)

    val timeString = date?.let { timeFormat.format(it) }

    val persianDate = getPersianDateTime(
        gregorianDate,
        inputFormat = GregorianDatePattern.PATTERN_1.text,
        PersianDatePattern.PATTERN_4.text
    )

    return "$persianDate $timeString"
}


/**
 * This function will get a string date in gregorian calendar and its format
 * and returns persian date in specified output format
 *
 * @param persianDate  persian date string
 * @param inputFormat  format of persianDate
 * @param outputFormat gregorian date format
 * @return gregorian date in specified output format
 */
fun getGregorianDateTime(
    persianDate: String,
    inputFormat: PersianDatePattern = PersianDatePattern.PATTERN_3,
    outputFormat: GregorianDatePattern = GregorianDatePattern.PATTERN_1,
    adjustTimeZone: Boolean = false
): String? {
    val pdFormatter = PersianDateFormat(inputFormat.text)

    try {
        val pDate = pdFormatter.parse(persianDate)
        val sdf = SimpleDateFormat(outputFormat.text, Locale.ENGLISH)

        if (adjustTimeZone) {
            val iranTimeZone = TimeZones.ASIA_TEHRAN
            val calendar = Calendar.getInstance(iranTimeZone.timeZone)
            calendar.time = pDate.toDate()

            sdf.timeZone = TimeZones.GMT.timeZone
            return sdf.format(calendar.time)
        }

        return sdf.format(pDate.toDate())
    } catch (ignored: Exception) {
    }

    return null
}

/**
 * Converts a Persian date string to a Gregorian date object.
 *
 * This function parses the provided `persianDate` string based on the specified `inputFormat` and then converts
 * it to a Gregorian `Date` object.
 *
 * @param persianDate The Persian date string to convert.
 * @param inputFormat The format of the `persianDate` string. Defaults to `PersianDatePattern.PATTERN_3`.
 * @return A `Date` object representing the Gregorian equivalent of the provided Persian date, or null if conversion fails.
 */
fun getGregorianDate(
    persianDate: String,
    inputFormat: PersianDatePattern = PersianDatePattern.PATTERN_3,
): Date? {
    val dateTime = getGregorianDateTime(persianDate, inputFormat)
    return getDate(dateTime)
}

/**
 * This function converts time in millisecond to dateTime using provided format
 *
 * @param timeInMillis time in millisecond
 * @return dateTime in provided format
 */
fun convertLongToDateTimeUTC(
    format: GregorianDatePattern = GregorianDatePattern.PATTERN_1,
    timeInMillis: Long
): String? {
    val dateTime = DateFormat.format(format.text, timeInMillis).toString()
    val sdf = SimpleDateFormat(format.text, Locale.ENGLISH)

    try {
        val date = sdf.parse(dateTime)
        sdf.timeZone = TimeZones.UTC.timeZone

        return sdf.format(date!!)

    } catch (ignored: Exception) {
    }

    return dateTime
}


/**
 * This function will convert a date from one date format to another one
 *
 * @param dateTime     input dateTime
 * @param inputFormat  input date format
 * @param outputFormat output date format
 * @param timeZone     output date timezone
 * @return date in output date format and timezone
 */
fun convertDateFormat(
    dateTime: String,
    inputFormat: GregorianDatePattern,
    outputFormat: GregorianDatePattern = GregorianDatePattern.PATTERN_1,
    timeZone: TimeZones? = null
): String? {
    try {
        val date = getDate(dateTime, inputFormat)
        val sdf = SimpleDateFormat(outputFormat.text, Locale.ENGLISH)

        timeZone?.let {
            sdf.timeZone = it.timeZone
        }

        return sdf.format(date!!)

    } catch (ignored: Exception) {
    }

    return null
}

/**
 * Retrieves the current date and time as a `Date` object.
 *
 * This function utilizes the internal `getTimeNow()` method to get the current system time in milliseconds
 * and then converts it to a `Date` object.
 *
 * @return The current date and time as a `Date` object.
 */
fun getDateNow(): Date {
    return Date(getTimeNow())
}

/**
 * Retrieves the current system time in milliseconds.
 *
 * The current system time obtained using `Date().time`.
 *
 * @return The current system time in milliseconds.
 */
fun getTimeNow(): Long {
    return Date().time
}

/**
 * This function will return current gregorian dateTime with provided format
 *
 * @return current dateTime
 */
fun getDateNow(format: GregorianDatePattern, timeZone: TimeZones = TimeZones.UTC): String {
    val sdf = SimpleDateFormat(format.text, Locale.ENGLISH)
    sdf.timeZone = timeZone.timeZone

    val currentDate = getDateNow()

    return sdf.format(currentDate)
}


/**
 * Gets the current date in the specified format.
 * @param format The date format.
 * @return The current date in the specified format.
 */
fun getCurrentPersianDate(format: PersianDatePattern = PersianDatePattern.PATTERN_1): String {
    val currentDate = getDateNow()
    return getPersianDateTime(currentDate, format)
}


/**
 * This function will check that dateTime is between start and end dates
 *
 * @param dateTime  dateTime
 * @param startDate start date
 * @param endDate   end date
 * @return is the dateTime between start and end dates
 */
fun isDateTimeBetween(dateTime: Date, startDate: Date?, endDate: Date?): Boolean {
    return dateTime.after(startDate) && dateTime.before(endDate)
}

/**
 * This function will parse date string and return Date object using provided format
 *
 * @param dateTime dateTime string
 * @param format   dateTime format
 * @param timeZone timeZone
 * @return Date object
 */
fun getDate(
    dateTime: String?,
    format: GregorianDatePattern = GregorianDatePattern.PATTERN_1,
    timeZone: TimeZones = TimeZones.UTC
): Date? {
    val dateFormat = SimpleDateFormat(format.text, Locale.ENGLISH)
    dateFormat.timeZone = timeZone.timeZone

    return try {
        if (dateTime != null) {
            dateFormat.parse(dateTime)
        } else null
    } catch (ignored: Exception) {
        null
    }

}

/**
 * This function will parse date string and return Date object using provided format
 *
 * @param dateTime date object
 * @param format   dateTime format
 * @return dateTime String
 */
fun getDate(
    dateTime: Date,
    format: GregorianDatePattern = GregorianDatePattern.PATTERN_1,
    timeZone: TimeZones? = TimeZones.UTC
): String? {
    val dateFormat = SimpleDateFormat(format.text, Locale.ENGLISH)

    timeZone?.let {
        dateFormat.timeZone = it.timeZone
    }

    return dateFormat.format(dateTime)
}


/**
 * This function will check if the first date is after second date in provided date format
 *
 * @param firstDate  first date
 * @param secondDate second date
 * @param format     date format
 * @return returns `true` if the first date is after second date, otherwise returns `false`
 */
fun isDateAfter(firstDate: String, secondDate: String, format: String): Boolean {
    var isDateAfter = false
    val sdf = SimpleDateFormat(format, Locale.ENGLISH)

    try {
        val dateSync = sdf.parse(secondDate)
        val dateNow = sdf.parse(firstDate)

        if (dateNow != null) isDateAfter = dateNow.after(dateSync)

    } catch (ignored: ParseException) {
    }

    return isDateAfter
}

/**
 * Adds the specified amount of time to the given `Date` object.
 *
 * @param amount the amount of time to add to the `date`.
 * @param field the field of the `Calendar` to modify, e.g., `Calendar.DAY_OF_MONTH`, `Calendar.HOUR`, etc.
 * @return a new `Date` object with the specified time added.
 */
fun Date.addTimeToDate(amount: Int, field: Int = Calendar.MINUTE): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(field, amount)
    return calendar.time
}

fun convertDateStringToMillis(dateString: String, format: String = GregorianDatePattern.PATTERN_6.text): Long? {
    return try {
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        sdf.timeZone = TimeZones.UTC.timeZone
        sdf.parse(dateString)?.time
    } catch (e: Exception) {
        null
    }
}

/**
 * Shows a Persian date picker using `MaterialDatePicker`.
 *
 * @param textView The [TextView] where the selected date (and optionally time) will be set.
 * @param title The title to be shown on the date picker dialog.
 * @param showTimePicker If `true`, a time picker will be shown after the user selects a date.
 */
fun FragmentManager.showDateTimePicker(
    textView: TextView,
    title: String,
    showTimePicker: Boolean = false,
    initialDateTime: String? = null // 1404/01/16 10:30
) {

    val (initialDate, initialTime) =
        initialDateTime?.split(" ")?.let { it.getOrNull(0) to it.getOrNull(1) } ?: (null to null)

    val grInitialDate = initialDate?.let { getGregorianDateTime(it) }



    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setEnableHoliday(true)
            .setPositiveButtonText(R.string.persian_picker_confirm)
            .setNegativeButtonText(R.string.persian_picker_cancel)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setSelection(100)
            //.setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

    datePicker.show(this, datePicker.toString())
    datePicker.addOnPositiveButtonClickListener { selectedDate ->
        val gregorianDate = Date(selectedDate)
        val persianDate = getPersianDateTime(
            gregorianDate,
            outputFormat = PersianDatePattern.PATTERN_4
        )
        if (showTimePicker) {
            textView.showLinearTimePicker(date = persianDate, initialTime = initialTime)
        } else {
            textView.text = persianDate
        }
    }
}

/**
 * Shows a linear (scrollable) Persian-style time picker.
 *
 * @param date An optional Persian date string to prefix the selected time.
 * @param title the title of time picker
 */
@SuppressLint("DefaultLocale", "SetTextI18n")
fun TextView.showLinearTimePicker(
    title: String = this.context.resources.getString(R.string.persian_picker_select_time),
    date: String? = null,
    initialTime: String? = null // 10:30
) {
    val timePicker = TimePickerBuilder(this.context)
        .setTitle(title)
        .setOnConfirmListener { selectedTime ->
            this.text = if (date.isNullOrEmpty()) selectedTime else "$date $selectedTime"
        }

    initialTime?.let {
        val parts = it.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        timePicker.setInitialTime(hour, minute)
    }
    timePicker.show()
}


/**
 * Sets the filter date-time for a given pair of TextInputEditText fields.
 *
 * @param editTextFrom The TextInputEditText for the from date-time.
 * @param editTextTo The TextInputEditText for the to date-time.
 * @param enableTime if true dateTime else date.
 */
fun setDateTimeFilterDefaults(
    editTextFrom: TextInputEditText,
    editTextTo: TextInputEditText,
    enableTime: Boolean = true
) {
    val currentDateTime = getDateNow()
    val startDate = subtractToMidnight(currentDateTime)
    val datePattern = if (enableTime) PersianDatePattern.PATTERN_5 else PersianDatePattern.PATTERN_2

    val formattedFromDate = getPersianDateTime(startDate, datePattern)
    val formattedToDate = getPersianDateTime(currentDateTime, datePattern)

    editTextFrom.setText(formattedFromDate)
    editTextTo.setText(formattedToDate)
}

/**
 * Subtracts 24 hours from the given date and returns the resulting date.
 *
 * @param date The input date from which 24 hours will be subtracted.
 * @return The resulting date after subtracting 24 hours.
 */
fun subtractToMidnight(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.time
}
