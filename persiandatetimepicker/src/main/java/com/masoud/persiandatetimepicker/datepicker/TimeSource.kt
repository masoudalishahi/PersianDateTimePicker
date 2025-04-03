package com.masoud.persiandatetimepicker.datepicker

/**
 * Provider for current time in milliseconds.
 */
internal class TimeSource private constructor(private val fixedTimeMs: Long?) {
    fun now(): Long {
        if (fixedTimeMs != null) {
            return fixedTimeMs
        }
        return System.currentTimeMillis()
    }

    companion object {
        private val SYSTEM_TIME_SOURCE = TimeSource(null)

        /**
         * A time source that returns the current system time.
         */
        @JvmStatic
        fun system(): TimeSource {
            return SYSTEM_TIME_SOURCE
        }

        /**
         * A fixed time source for testing.
         */
        fun fixed(epochMs: Long): TimeSource {
            return TimeSource(epochMs)
        }
    }
}
