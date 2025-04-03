package com.masoud.persiandatetimepicker.datepicker

import com.masoud.persiandatetimepicker.datepicker.TimeSource.Companion.system
import com.masoud.persiandatetimepicker.datepicker.UtcDates.utcCalendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.atomic.AtomicReference

/**
 * Utility class for date/time operations. بدون استفاده از میلادی.
 */
internal object UtcDates {
    const val TEHRAN: String = "Asia/Tehran"
    val PERSIAN_LOCALE: Locale = Locale("fa", "IR")

    var timeSourceRef: AtomicReference<TimeSource?> = AtomicReference<TimeSource?>()

    var timeSource: TimeSource?
        get() {
            val ts =
                timeSourceRef.get()
            return ts ?: system()
        }
        set(timeSource) {
            timeSourceRef.set(timeSource)
        }

    private val timeZone: TimeZone by lazy { TimeZone.getTimeZone(TEHRAN) }

    private val utcCalendarInstance by lazy {
        PersianCalendarHelper().apply { clear() }
    }

    fun utcCalendar(): PersianCalendarHelper = utcCalendarInstance

    val todayCanonical: Long by lazy {
        canonicalYearMonthDay(todayCalendar.timeInMillis)
    }

    @JvmStatic
    val todayCalendar: PersianCalendarHelper
        get() = PersianCalendarHelper(System.currentTimeMillis())

    @JvmStatic
    val utcCalendar: PersianCalendarHelper
        get() {
            val helper = PersianCalendarHelper()
            helper.clear()
            return helper
        }

    fun getUtcCalendarOf(raw: PersianCalendarHelper?): PersianCalendarHelper {
        val c = PersianCalendarHelper()
        if (raw == null) {
            c.clear()
        } else {
            c.timeInMillis = raw.timeInMillis
        }
        return c
    }

    @JvmStatic
    fun getDayCopy(raw: PersianCalendarHelper): PersianCalendarHelper {
        return PersianCalendarHelper(raw.timeInMillis)
    }

    @JvmStatic
    fun canonicalYearMonthDay(rawDate: Long): Long {
        val cal = utcCalendar()
        cal.timeInMillis = rawDate
        return getDayCopy(cal).timeInMillis
    }

    @JvmStatic
    val defaultTextInputHint: String
        get() = "yyyy/MM/dd"
}
