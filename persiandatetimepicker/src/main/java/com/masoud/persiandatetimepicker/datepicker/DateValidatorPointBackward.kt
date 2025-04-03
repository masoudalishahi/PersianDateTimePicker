package com.masoud.persiandatetimepicker.datepicker

import android.os.Parcel
import android.os.Parcelable
import com.masoud.persiandatetimepicker.datepicker.UtcDates.todayCalendar

/**
 * A [CalendarConstraints.DateValidator] that enables only dates before a given point.
 * Defaults to the current moment in device time backwards using [ ][DateValidatorPointBackward.now], but can be set to any point, as UTC milliseconds, using [ ][DateValidatorPointBackward.before].
 */
class DateValidatorPointBackward private constructor(private val point: Long) :
    CalendarConstraints.DateValidator {
    override fun isValid(date: Long): Boolean {
        return date <= point
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(point)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is DateValidatorPointBackward) {
            return false
        }

        return point == o.point
    }

    override fun hashCode(): Int {
        val hashedFields = arrayOf<Any?>(point)
        return hashedFields.contentHashCode()
    }

    companion object {
        /**
         * Returns a [CalendarConstraints.DateValidator] which enables only days before `point`, in UTC milliseconds.
         */
        fun before(point: Long): DateValidatorPointBackward {
            return DateValidatorPointBackward(point)
        }

        /**
         * Returns a [CalendarConstraints.DateValidator] enabled from the current moment in device
         * time backwards.
         */
        fun now(): DateValidatorPointBackward {
            return before(todayCalendar.timeInMillis)
        }

        /** Part of [Parcelable] requirements. Do not use.  */
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<DateValidatorPointBackward?> =
            object : Parcelable.Creator<DateValidatorPointBackward?> {
                override fun createFromParcel(source: Parcel): DateValidatorPointBackward {
                    return DateValidatorPointBackward(source.readLong())
                }

                override fun newArray(size: Int): Array<DateValidatorPointBackward?> {
                    return arrayOfNulls<DateValidatorPointBackward>(size)
                }
            }
    }
}
