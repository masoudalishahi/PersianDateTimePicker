package com.masoud.persiandatetimepicker.datepicker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Parcelable

/**
 * A decorator which allows customizing the day of month views within a [MaterialDatePicker].
 */
@Suppress("unused")
abstract class DayViewDecorator : Parcelable {
    /**
     * Optionally override this method to do any initializing for your `DayViewDecorator`
     * instance.
     *
     *
     * This method will be called whenever the date picker view is created, which can be important
     * if, e.g., your decorator's compound drawables are dependent on configurations such as screen
     * orientation.
     */
    fun initialize(context: Context) {}

    fun getCompoundDrawableLeft(
        context: Context, year: Int, month: Int, day: Int, valid: Boolean, selected: Boolean
    ): Drawable? {
        return null
    }

    fun getCompoundDrawableTop(
        context: Context, year: Int, month: Int, day: Int, valid: Boolean, selected: Boolean
    ): Drawable? {
        return null
    }

    fun getCompoundDrawableRight(
        context: Context, year: Int, month: Int, day: Int, valid: Boolean, selected: Boolean
    ): Drawable? {
        return null
    }

    fun getCompoundDrawableBottom(
        context: Context, year: Int, month: Int, day: Int, valid: Boolean, selected: Boolean
    ): Drawable? {
        return null
    }

    fun getBackgroundColor(
        context: Context, year: Int, month: Int, day: Int, valid: Boolean, selected: Boolean
    ): ColorStateList? {
        return null
    }

    open fun getTextColor(
        context: Context, year: Int, month: Int, day: Int, valid: Boolean, selected: Boolean
    ): ColorStateList? {
        return null
    }

    fun getContentDescription(
        context: Context,
        year: Int,
        month: Int,
        day: Int,
        valid: Boolean,
        selected: Boolean,
        originalContentDescription: CharSequence?
    ): CharSequence? {
        return originalContentDescription
    }
}
