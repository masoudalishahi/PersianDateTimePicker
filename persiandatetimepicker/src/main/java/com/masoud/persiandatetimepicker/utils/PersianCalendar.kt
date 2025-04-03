package com.masoud.persiandatetimepicker.utils

import java.io.Serial
import java.util.GregorianCalendar
import java.util.TimeZone
import kotlin.math.floor

class PersianCalendar : GregorianCalendar {
    private var persianYear = 0
    private var persianMonth = 0
    private var persianDay = 0
    var delimiter: String = "/"

    private fun convertToMillis(julianDate: Long): Long {
        return (PersianCalendarConstants.MILLIS_JULIAN_EPOCH + julianDate * PersianCalendarConstants.MILLIS_OF_A_DAY + PersianCalendarUtils.ceil(
            (timeInMillis - PersianCalendarConstants.MILLIS_JULIAN_EPOCH).toDouble(),
            PersianCalendarConstants.MILLIS_OF_A_DAY.toDouble()
        ))
    }

    constructor(millis: Long) {
        setTimeInMillis(millis)
    }

    constructor() {
        setTimeZone(TimeZone.getTimeZone("GMT"))
    }

    private fun calculatePersianDate() {
        val julianDate =
            (floor((timeInMillis - PersianCalendarConstants.MILLIS_JULIAN_EPOCH).toDouble()).toLong() / PersianCalendarConstants.MILLIS_OF_A_DAY)
        val persianRowDate = PersianCalendarUtils.julianToPersian(julianDate)
        val year = persianRowDate shr 16
        val month = (persianRowDate and 0xff00L).toInt() shr 8
        val day = (persianRowDate and 0xffL).toInt()
        this.persianYear = (if (year > 0) year else year - 1).toInt()
        this.persianMonth = month
        this.persianDay = day
    }

    val isPersianLeapYear: Boolean
        get() =
            PersianCalendarUtils.isPersianLeapYear(this.persianYear)

    fun setPersianDate(persianYear: Int, persianMonth: Int, persianDay: Int) {
        var persianMonth = persianMonth
        persianMonth += 1
        this.persianYear = persianYear
        this.persianMonth = persianMonth
        this.persianDay = persianDay
        setTimeInMillis(
            convertToMillis(
                PersianCalendarUtils.persianToJulian(
                    (if (this.persianYear > 0) this.persianYear else this.persianYear + 1).toLong(),
                    this.persianMonth - 1,
                    this.persianDay
                )
            )
        )
    }

    fun setPersianYear(persianYear: Int) {
        this.persianMonth += 1
        this.persianYear = persianYear
        setTimeInMillis(
            convertToMillis(
                PersianCalendarUtils.persianToJulian(
                    (if (this.persianYear > 0) this.persianYear else this.persianYear + 1).toLong(),
                    this.persianMonth - 1,
                    this.persianDay
                )
            )
        )
    }

    fun setPersianMonth(persianMonth: Int) {
        var persianMonth = persianMonth
        persianMonth += 1
        this.persianMonth = persianMonth
        setTimeInMillis(
            convertToMillis(
                PersianCalendarUtils.persianToJulian(
                    (if (this.persianYear > 0) this.persianYear else this.persianYear + 1).toLong(),
                    this.persianMonth - 1,
                    1
                )
            )
        )
    }

    fun setPersianDay(persianDay: Int) {
        this.persianMonth += 1
        this.persianDay = persianDay
        setTimeInMillis(
            convertToMillis(
                PersianCalendarUtils.persianToJulian(
                    (if (this.persianYear > 0) this.persianYear else this.persianYear + 1).toLong(),
                    this.persianMonth - 1,
                    this.persianDay
                )
            )
        )
    }

    fun getPersianYear(): Int {
        return this.persianYear
    }

    fun getPersianMonth(): Int {
        return this.persianMonth
    }

    val persianMonthName: String?
        get() = PersianCalendarConstants.persianMonthNames[this.persianMonth]

    fun getPersianDay(): Int {
        return this.persianDay
    }

    val persianWeekDayName: String?
        get() = when (get(DAY_OF_WEEK)) {
            SATURDAY -> PersianCalendarConstants.persianWeekDays[0]
            SUNDAY -> PersianCalendarConstants.persianWeekDays[1]
            MONDAY -> PersianCalendarConstants.persianWeekDays[2]
            TUESDAY -> PersianCalendarConstants.persianWeekDays[3]
            WEDNESDAY -> PersianCalendarConstants.persianWeekDays[4]
            THURSDAY -> PersianCalendarConstants.persianWeekDays[5]
            else -> PersianCalendarConstants.persianWeekDays[6]
        }

    val persianLongDate: String
        get() = this.persianWeekDayName + "  " + this.persianDay + "  " + this.persianMonthName + "  " + this.persianYear

    val persianLongDateAndTime: String
        get() = this.persianLongDate + " ساعت " + get(HOUR_OF_DAY) + ":" + get(
            MINUTE
        ) + ":" + get(SECOND)

    val persianShortDate: String
        get() =// calculatePersianDate();
            formatToMilitary(this.persianYear) + delimiter + formatToMilitary(getPersianMonth() + 1) + delimiter + formatToMilitary(
                this.persianDay
            )

    val persianShortDateTime: String
        get() = (formatToMilitary(this.persianYear) + delimiter + formatToMilitary(getPersianMonth() + 1) + delimiter + formatToMilitary(
            this.persianDay
        ) + " " + formatToMilitary(this.get(HOUR_OF_DAY)) + ":" + formatToMilitary(
            get(MINUTE)
        )
                + ":" + formatToMilitary(get(SECOND)))

    private fun formatToMilitary(i: Int): String {
        return if (i <= 9) "0$i" else i.toString()
    }

    fun addPersianDate(field: Int, amount: Int) {
        if (amount == 0) {
            return
        }

        require(!(field < 0 || field >= ZONE_OFFSET))

        if (field == YEAR) {
            setPersianDate(this.persianYear + amount, getPersianMonth() + 1, this.persianDay)
            return
        } else if (field == MONTH) {
            setPersianDate(
                this.persianYear + ((getPersianMonth() + 1 + amount) / 12),
                (getPersianMonth() + 1 + amount) % 12,
                this.persianDay
            )
            return
        }
        add(field, amount)
        calculatePersianDate()
    }

    fun addPersianDate1(field: Int, amount: Int) {
        if (amount == 0) {
            return
        }

        require(!(field < 0 || field >= ZONE_OFFSET))

        if (field == YEAR) {
            setPersianDate(this.persianYear + amount, getPersianMonth() + 1, 1)
            return
        } else if (field == MONTH) {
            setPersianDate(
                this.persianYear + ((getPersianMonth() + 1 + amount) / 12),
                (getPersianMonth() + 1 + amount) % 12,
                this.persianDay
            )

            return
        }

        calculatePersianDate()
    }

    fun parse(dateString: String?) {
        val p: PersianCalendar = PersianDateParser(dateString!!, delimiter).persianDate
        setPersianDate(p.getPersianYear(), p.getPersianMonth(), p.getPersianDay())
    }

    override fun toString(): String {
        val str = super.toString()
        return str.substring(0, str.length - 1) + ",PersianDate=" + this.persianShortDate + "]"
    }

    override fun equals(obj: Any?): Boolean {
        return super.equals(obj)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun set(field: Int, value: Int) {
        super.set(field, value)
        calculatePersianDate()
    }

    override fun setTimeInMillis(millis: Long) {
        super.setTimeInMillis(millis)
        calculatePersianDate()
    }

    @Suppress("WRONG_NULLABILITY_FOR_JAVA_OVERRIDE")
    override fun setTimeZone(zone: TimeZone?) {
        if (zone != null) {
            super.setTimeZone(zone)
        }
        calculatePersianDate()
    }

    companion object {
        @Serial
        private const val serialVersionUID = 5541422440580682494L
    }
}