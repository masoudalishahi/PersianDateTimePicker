package com.masoud.persiandatetimepicker.datepicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import com.google.android.material.resources.MaterialAttributes
import com.google.android.material.resources.MaterialResources
import com.masoud.persiandatetimepicker.R
import com.masoud.persiandatetimepicker.datepicker.CalendarItemStyle.Companion.create

/**
 * Data class for loaded `R.styleable.MaterialCalendar` and `R.styleable.MaterialCalendarItem` attributes.
 */
@SuppressLint("RestrictedApi", "UseKtx")
internal class CalendarStyle @SuppressLint("PrivateResource") constructor(context: Context) {
    /**
     * The `R.styleable.MaterialCalendarItem` style for days with no unique characteristics from
     * `R.styleable.MaterialCalendar_dayStyle`.
     */
    @JvmField
    val day: CalendarItemStyle

    /**
     * The `R.styleable.MaterialCalendarItem` style for selected days from `R.styleable.MaterialCalendar_daySelectedStyle`.
     */
    @JvmField
    val selectedDay: CalendarItemStyle

    /**
     * The `R.styleable.MaterialCalendarItem` style for today from `R.styleable.MaterialCalendar_dayTodayStyle`.
     */
    @JvmField
    val todayDay: CalendarItemStyle

    /**
     * The `R.styleable.MaterialCalendarItem` style for years with no unique characteristics
     * from `R.styleable#MaterialCalendar_yearStyle`.
     */
    @JvmField
    val year: CalendarItemStyle

    /**
     * The `R.styleable.MaterialCalendarItem` style for selected years from `R.styleable.MaterialCalendar_yearSelectedStyle`.
     */
    val selectedYear: CalendarItemStyle

    /**
     * The `R.styleable.MaterialCalendarItem` style for today's year from `R.styleable.MaterialCalendar_yearTodayStyle`.
     */
    val todayYear: CalendarItemStyle

    @JvmField
    val invalidDay: CalendarItemStyle

    /**
     * A [Paint] for styling days between selected days with [ ][R.styleable.MaterialCalendar_rangeFillColor].
     */
    @JvmField
    val rangeFill: Paint

    init {
        val calendarStyle =
            MaterialAttributes.resolveOrThrow(
                context,
                R.attr.materialCalendarStyle,
                MaterialCalendar::class.java.getCanonicalName()
            )
        val calendarAttributes =
            context.obtainStyledAttributes(calendarStyle, R.styleable.MaterialCalendar)

        day =
            create(
                context, calendarAttributes.getResourceId(R.styleable.MaterialCalendar_dayStyle, 0)
            )
        invalidDay =
            create(
                context,
                calendarAttributes.getResourceId(R.styleable.MaterialCalendar_dayInvalidStyle, 0)
            )
        selectedDay =
            create(
                context,
                calendarAttributes.getResourceId(R.styleable.MaterialCalendar_daySelectedStyle, 0)
            )
        todayDay =
            create(
                context,
                calendarAttributes.getResourceId(R.styleable.MaterialCalendar_dayTodayStyle, 0)
            )
        val rangeFillColorList =
            MaterialResources.getColorStateList(
                context, calendarAttributes, R.styleable.MaterialCalendar_rangeFillColor
            )

        year =
            create(
                context, calendarAttributes.getResourceId(R.styleable.MaterialCalendar_yearStyle, 0)
            )
        selectedYear =
            create(
                context,
                calendarAttributes.getResourceId(R.styleable.MaterialCalendar_yearSelectedStyle, 0)
            )
        todayYear =
            create(
                context,
                calendarAttributes.getResourceId(R.styleable.MaterialCalendar_yearTodayStyle, 0)
            )

        rangeFill = Paint()
        rangeFill.setColor(rangeFillColorList!!.defaultColor)

        calendarAttributes.recycle()
    }
}
