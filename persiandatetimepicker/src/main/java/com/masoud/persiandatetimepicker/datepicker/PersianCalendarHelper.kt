package com.masoud.persiandatetimepicker.datepicker

import saman.zamani.persiandate.PersianDate


/**
 * A helper class to replace old ICU Calendar usage with Samanzamani's PersianDate library.
 * Keeps the logic consistent with the original usage for YEAR, MONTH, DAY_OF_MONTH, etc.
 */
class PersianCalendarHelper {
    private var persianDate: PersianDate

    constructor() {
        this.persianDate = PersianDate()
    }

    constructor(epochMs: Long) {
        this.persianDate = PersianDate(epochMs)
    }

    var timeInMillis: Long
        /**
         * Returns the stored time in milliseconds from epoch.
         */
        get() = persianDate.time
        /**
         * Sets time in milliseconds from epoch (Unix time).
         */
        set(epochMs) {
            persianDate = PersianDate(epochMs)
        }

    /**
     * Clear or reset to a default baseline date.
     * For example, we set to 1970-01-01 (1348-10-11 approx).
     */
    fun clear() {
        this.persianDate = PersianDate()
        this.persianDate.setShYear(1348)
        this.persianDate.setShMonth(10)
        this.persianDate.setShDay(11)
    }

    /**
     * Sets a field (YEAR, MONTH (0-based), DAY_OF_MONTH, etc.) to a value.
     */
    fun set(field: Int, value: Int) {
        when (field) {
            YEAR -> persianDate.setShYear(value)
            MONTH ->                 // old code is 0..11, library uses 1..12
                persianDate.setShMonth(value + 1)

            DAY_OF_MONTH -> persianDate.setShDay(value)
            else -> {}
        }
    }

    /**
     * Adds an amount to a field (e.g., add to YEAR or MONTH).
     */
    fun add(field: Int, amount: Int) {
        when (field) {
            MONTH -> {
                val currentMonth = persianDate.shMonth
                val currentYear = persianDate.shYear
                var newMonth = currentMonth + amount
                var newYear = currentYear
                while (newMonth < 1) {
                    newYear--
                    newMonth += 12
                }
                while (newMonth > 12) {
                    newYear++
                    newMonth -= 12
                }
                persianDate.setShYear(newYear)
                persianDate.setShMonth(newMonth)
            }

            YEAR -> persianDate.setShYear(persianDate.shYear + amount)
            else -> {}
        }
    }

    /**
     * Retrieves the specified field's value (YEAR, MONTH (0-based), etc.).
     */
    fun get(field: Int): Int {
        return when (field) {
            YEAR -> persianDate.shYear
            MONTH ->  // Convert 1..12 to 0..11
                persianDate.shMonth - 1

            DAY_OF_MONTH -> persianDate.shDay
            DAY_OF_WEEK ->  // Samanzamani: dayOfWeek => 1=Saturday ... 7=Friday
                persianDate.dayOfWeek()

            WEEK_OF_MONTH -> {
                val day = persianDate.shDay
                ((day - 1) / 7) + 1
            }

            else -> 0
        }
    }

    /**
     * Returns the maximum possible value for some fields.
     */
    fun getMaximum(field: Int): Int {
        if (field == DAY_OF_WEEK) {
            return 7
        }
        return 0
    }

    /**
     * Returns the actual maximum for fields like DAY_OF_MONTH in the current month/year.
     */
    fun getActualMaximum(field: Int): Int {
        if (field == DAY_OF_MONTH) {
            val temp = PersianDate()
            temp.setShYear(persianDate.shYear)
            temp.setShMonth(persianDate.shMonth)
            temp.setShDay(1)
            return temp.monthLength
        }
        return 0
    }

    companion object {
        // Constants matching typical Calendar fields
        const val YEAR: Int = 1
        const val MONTH: Int = 2
        const val DAY_OF_MONTH: Int = 5
        const val DAY_OF_WEEK: Int = 7
        const val WEEK_OF_MONTH: Int = 8
    }
}
