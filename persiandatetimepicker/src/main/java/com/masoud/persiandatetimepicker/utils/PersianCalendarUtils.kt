package com.masoud.persiandatetimepicker.utils

import kotlin.math.floor

object PersianCalendarUtils {
    @JvmStatic
    fun persianToJulian(year: Long, month: Int, day: Int): Long {
        return 365L *
                ((ceil((year - 474L).toDouble(), 2820.0) + 474L) - 1L) +
                (floor(
                    (682L * (ceil(
                        (year - 474L).toDouble(),
                        2820.0
                    ) + 474L) - 110L) / 2816.0
                ).toLong()) +
                (PersianCalendarConstants.PERSIAN_EPOCH - 1L) + 1029983L *
                (floor((year - 474L) / 2820.0).toLong()) +
                (if (month < 7) 31L * month else 30L * month + 6) + day
    }

    @JvmStatic
    fun isPersianLeapYear(persianYear: Int): Boolean {
        return ceil(
            (38.0 + (ceil((persianYear - 474L).toDouble(), 2820.0) + 474L)) * 682.0,
            2816.0
        ) < 682L
    }

    @JvmStatic
    fun julianToPersian(julianDate: Long): Long {
        val persianEpochInJulian = julianDate - persianToJulian(475L, 0, 1)
        val cyear = ceil(persianEpochInJulian.toDouble(), 1029983.0)
        val ycycle =
            if (cyear != 1029982L) (floor((2816.0 * cyear.toDouble() + 1031337.0) / 1028522.0).toLong()) else 2820L
        val year = 474L + 2820L * (floor(persianEpochInJulian / 1029983.0).toLong()) + ycycle
        val aux = (1L + julianDate) - persianToJulian(year, 0, 1)
        val month =
            (if (aux > 186L) kotlin.math.ceil((aux - 6L).toDouble() / 30.0) - 1 else kotlin.math.ceil(
                aux.toDouble() / 31.0
            ) - 1).toInt()
        val day = (julianDate - (persianToJulian(year, month, 1) - 1L)).toInt()
        return (year shl 16) or (month.toLong() shl 8) or day.toLong()
    }

    @JvmStatic
    fun ceil(double1: Double, double2: Double): Long {
        return (double1 - double2 * floor(double1 / double2)).toLong()
    }
}