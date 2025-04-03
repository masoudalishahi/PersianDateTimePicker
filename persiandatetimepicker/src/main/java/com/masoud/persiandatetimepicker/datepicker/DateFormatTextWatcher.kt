package com.masoud.persiandatetimepicker.datepicker

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.textfield.TextInputLayout
import com.masoud.persiandatetimepicker.R
import saman.zamani.persiandate.PersianDate

@SuppressLint("RestrictedApi")
abstract class DateFormatTextWatcher(
    private val pattern: String,
    private val textInputLayout: TextInputLayout,
    private val constraints: CalendarConstraints
) : TextWatcherAdapter() {

    private val outOfRange: String =
        textInputLayout.context.getString(R.string.mtrl_picker_out_of_range)

    private val setErrorCallback: Runnable = Runnable {
        val invalidFormat = textInputLayout.context.getString(R.string.mtrl_picker_invalid_format)
        val useLine = String.format(
            textInputLayout.context.getString(R.string.mtrl_picker_invalid_format_use),
            pattern
        )
        val exampleLine = String.format(
            textInputLayout.context.getString(R.string.mtrl_picker_invalid_format_example),
            "1402/07/10"
        )
        textInputLayout.error = "$invalidFormat\n$useLine\n$exampleLine"
        onInvalidDate()
    }

    private var setRangeErrorCallback: Runnable? = null

    private val persianDate by lazy { PersianDate() }

    abstract fun onValidDate(day: Long?)

    open fun onInvalidDate() {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        textInputLayout.removeCallbacks(setErrorCallback)
        setRangeErrorCallback?.let { textInputLayout.removeCallbacks(it) }
        textInputLayout.error = null
        onValidDate(null)

        if (TextUtils.isEmpty(s) || s.length < pattern.length) {
            return
        }
        try {
            val millis = parseJalali(s.toString())
            val validator = constraints.dateValidator
            if (validator.isValid(millis) && constraints.isWithinBounds(millis)) {
                onValidDate(millis)
            } else {
                setRangeErrorCallback = createRangeErrorCallback(millis)
                runValidation(textInputLayout, setRangeErrorCallback!!)
            }
        } catch (_: Exception) {
            runValidation(textInputLayout, setErrorCallback)
        }
    }

    private fun createRangeErrorCallback(millis: Long): Runnable {
        return Runnable {
            // outOfRange
            val dateStr = DateStrings.getDateString(millis)
            textInputLayout.error = String.format(outOfRange, dateStr)
            onInvalidDate()
        }
    }

    fun runValidation(view: View, validation: Runnable) {
        view.post(validation)
    }

    @Throws(IllegalArgumentException::class)
    private fun parseJalali(text: String): Long {
        val parts = text.split("/")
        if (parts.size != 3) {
            throw IllegalArgumentException("invalid format for jalali: $text")
        }

        persianDate.shYear = parts[0].toInt()
        persianDate.shMonth = parts[1].toInt()
        persianDate.shDay = parts[2].toInt()

        return persianDate.time
    }
}
