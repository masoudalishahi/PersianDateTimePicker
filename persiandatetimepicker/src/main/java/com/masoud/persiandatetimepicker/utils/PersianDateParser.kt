package com.masoud.persiandatetimepicker.utils

class PersianDateParser private constructor(var dateString: String) {
    var delimiter: String = "/"

    internal constructor(dateString: String, delimiter: String) : this(dateString) {
        this.delimiter = delimiter
    }

    val persianDate: PersianCalendar
        get() {
            checkDateStringInitialValidation()

            val tokens: Array<String?>? = splitDateString(normalizeDateString(dateString)!!)
            val year = tokens!![0]!!.toInt()
            val month = tokens[1]!!.toInt()
            val day = tokens[2]!!.toInt()

            checkPersianDateValidation(year, month, day)

            val pCal = PersianCalendar()
            pCal.setPersianDate(year, month, day)

            return pCal
        }

    private fun checkPersianDateValidation(year: Int, month: Int, day: Int) {
        if (year < 1) throw RuntimeException("year is not valid")
        if (month < 1 || month > 12) throw RuntimeException("month is not valid")
        if (day < 1 || day > 31) throw RuntimeException("day is not valid")
        if (month > 6 && day == 31) throw RuntimeException("day is not valid")
        if (month == 12 && day == 30 && !PersianCalendarUtils.isPersianLeapYear(year)) throw RuntimeException(
            "day is not valid $year is not a leap year"
        )
    }

    private fun normalizeDateString(dateString: String?): String? {
        return dateString
    }

    private fun splitDateString(dateString: String): Array<String?> {
        val tokens: Array<String?>? =
            dateString.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (tokens!!.size != 3) throw RuntimeException("wrong date:$dateString is not a Persian Date or can not be parsed")

        return tokens
    }

    private fun checkDateStringInitialValidation() {
        if (false) throw RuntimeException("input didn't assign please use setDateString()")
    }
}