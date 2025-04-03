package com.masoud.persiandatetimepicker.timepicker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.masoud.persiandatetimepicker.R

class TimePickerBuilder(private val context: Context) {
    private var selectedHour = 0
    private var selectedMinute = 0
    private var title: String? = null
    private var confirmListener: ((String) -> Unit)? = null
    private var cancelListener: (() -> Unit)? = null

    fun setInitialTime(hour: Int, minute: Int): TimePickerBuilder {
        this.selectedHour = hour
        this.selectedMinute = minute
        return this
    }

    fun setTitle(title: String): TimePickerBuilder {
        this.title = title
        return this
    }

    fun setOnConfirmListener(listener: (String) -> Unit): TimePickerBuilder {
        this.confirmListener = listener
        return this
    }

    fun setOnCancelListener(listener: () -> Unit): TimePickerBuilder {
        this.cancelListener = listener
        return this
    }

    @SuppressLint("DefaultLocale", "InflateParams", "SetTextI18n")
    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_linear_time_picker, null)
        val dialog = BottomSheetDialog(context).apply {
            setContentView(view)
        }

        val hourPicker = view.findViewById<NumberPicker>(R.id.hour_picker)
        val minutePicker = view.findViewById<NumberPicker>(R.id.minute_picker)
        val txtConfirm = view.findViewById<TextView>(R.id.txt_confirm)
        val txtCancel = view.findViewById<TextView>(R.id.txt_cancel)
        val txtTitle = view.findViewById<TextView>(R.id.txt_time_title)

        val formatter = NumberPicker.Formatter { value -> String.format("%02d", value) }

        fun updateTitle() {
            txtTitle.text = title ?: "%02d:%02d".format(selectedHour, selectedMinute)
        }

        hourPicker.initPicker(0, 23, selectedHour, formatter) { newVal ->
            selectedHour = newVal
            if (title == null) updateTitle()
        }

        minutePicker.initPicker(0, 59, selectedMinute, formatter) { newVal ->
            selectedMinute = newVal
            if (title == null) updateTitle()
        }

        updateTitle()

        txtConfirm.setOnClickListener {
            val timeStr = "%02d:%02d".format(selectedHour, selectedMinute)
            confirmListener?.invoke(timeStr)
            dialog.dismiss()
        }

        txtCancel.setOnClickListener {
            cancelListener?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun NumberPicker.initPicker(
        min: Int,
        max: Int,
        value: Int,
        formatter: NumberPicker.Formatter,
        onChange: (newVal: Int) -> Unit
    ) {
        minValue = min
        maxValue = max
        this.value = value
        setFormatter(formatter)
        setOnValueChangedListener { _, _, newVal -> onChange(newVal) }
    }
}
