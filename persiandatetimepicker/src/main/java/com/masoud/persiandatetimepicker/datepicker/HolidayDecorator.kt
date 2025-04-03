package com.masoud.persiandatetimepicker.datepicker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import saman.zamani.persiandate.PersianDate

class HolidayDecorator() : DayViewDecorator() {
    @Suppress("unused")
    constructor(parcel: Parcel) : this()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {}

    override fun getTextColor(
        context: Context,
        year: Int,
        month: Int,
        day: Int,
        valid: Boolean,
        selected: Boolean
    ): ColorStateList? {
        val persianDate = PersianDate()
        persianDate.shYear = year
        persianDate.shMonth = month + 1
        persianDate.shDay = day

        return if (
            persianDate.dayOfWeek() == 6 ||
            (persianDate.shMonth == 1 && day in listOf(1, 2, 3, 4, 13)) ||
            (persianDate.shMonth == 3 && day in listOf(14,15)) ||
            (persianDate.shMonth == 11 && day in listOf(22)) ||
            (persianDate.shMonth == 12 && day in listOf(29))
        ) {
            ColorStateList.valueOf(Color.RED)
        } else {
            null
        }
    }

    companion object CREATOR : Parcelable.Creator<HolidayDecorator> {
        override fun createFromParcel(parcel: Parcel): HolidayDecorator {
            return HolidayDecorator(parcel)
        }

        override fun newArray(size: Int): Array<HolidayDecorator?> {
            return arrayOfNulls(size)
        }
    }
}
