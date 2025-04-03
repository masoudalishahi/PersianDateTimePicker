package com.masoud.persiandatetimepicker.datepicker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import com.google.android.material.resources.MaterialAttributes
import com.google.android.material.textfield.TextInputLayout
import com.masoud.persiandatetimepicker.R
import com.masoud.persiandatetimepicker.datepicker.UtcDates.defaultTextInputHint
import saman.zamani.persiandate.PersianDate
import java.text.SimpleDateFormat
import kotlin.math.min

/**
 * A DateSelector that uses a pair of Longs to represent a range, all in Jalali.
 * Text field uses "yyyy/MM/dd"
 * Header uses "20 فروردین 1404" style.
 */
class RangeDateSelector : DateSelector<Pair<Long?, Long?>?> {
    private var error: CharSequence? = null
    private var invalidRangeStartError: String? = null
    private val invalidRangeEndError = " "
    private var selectedStartItem: Long? = null
    private var selectedEndItem: Long? = null
    private var proposedTextStart: Long? = null
    private var proposedTextEnd: Long? = null

    override fun select(selection: Long) {
        if (selectedStartItem == null) {
            selectedStartItem = selection
        } else if (selectedEndItem == null && isValidRange(selectedStartItem!!, selection)) {
            selectedEndItem = selection
        } else {
            selectedEndItem = null
            selectedStartItem = selection
        }
    }

    override fun isSelectionComplete(): Boolean {
        return (selectedStartItem != null && selectedEndItem != null && isValidRange(
            selectedStartItem!!, selectedEndItem!!
        ))
    }

    override fun setSelection(selection: Pair<Long?, Long?>) {
        if (selection.first != null && selection.second != null) {
            require(isValidRange(selection.first!!, selection.second!!)) { "Invalid range" }
        }
        selectedStartItem = if (selection.first == null) null else UtcDates.canonicalYearMonthDay(
            selection.first!!
        )
        selectedEndItem = if (selection.second == null) null else UtcDates.canonicalYearMonthDay(
            selection.second!!
        )
    }

    override fun getSelection(): Pair<Long?, Long?> {
        return Pair<Long?, Long?>(selectedStartItem, selectedEndItem)
    }

    override fun getSelectedRanges(): MutableCollection<Pair<Long?, Long?>?> {
        val lst = ArrayList<Pair<Long?, Long?>?>()
        lst.add(Pair<Long?, Long?>(selectedStartItem, selectedEndItem))
        return lst
    }

    override fun getSelectedDays(): MutableCollection<Long?> {
        val r = ArrayList<Long?>()
        if (selectedStartItem != null) r.add(selectedStartItem)
        if (selectedEndItem != null) r.add(selectedEndItem)
        return r
    }

    @SuppressLint("RestrictedApi")
    override fun getDefaultThemeResId(context: Context): Int {
        val res = context.resources
        val maximumDefaultFullscreenMinorAxis =
            res.getDimensionPixelSize(R.dimen.mtrl_calendar_maximum_default_fullscreen_minor_axis)
        val widthPx = res.displayMetrics.widthPixels
        val heightPx = res.displayMetrics.heightPixels
        val minorAxis = min(widthPx.toDouble(), heightPx.toDouble()).toInt()
        val defThemeAttr = if (minorAxis > maximumDefaultFullscreenMinorAxis)
            R.attr.materialCalendarTheme
        else
            R.attr.materialCalendarFullscreenTheme
        return MaterialAttributes.resolveOrThrow(
            context, defThemeAttr, MaterialDatePicker::class.java.canonicalName
        )
    }

    override fun getSelectionDisplayString(context: Context): String {
        val res = context.resources
        if (selectedStartItem == null && selectedEndItem == null) {
            return res.getString(R.string.mtrl_picker_range_header_unselected)
        }
        if (selectedEndItem == null) {
            val startStr = jalaliFormatLongMonth(selectedStartItem!!)
            return res.getString(R.string.mtrl_picker_range_header_only_start_selected, startStr)
        }
        if (selectedStartItem == null) {
            val endStr = jalaliFormatLongMonth(selectedEndItem!!)
            return res.getString(R.string.mtrl_picker_range_header_only_end_selected, endStr)
        }
        val startStr = jalaliFormatLongMonth(selectedStartItem!!)
        val endStr = jalaliFormatLongMonth(selectedEndItem!!)
        return res.getString(R.string.mtrl_picker_range_header_selected, startStr, endStr)
    }

    override fun getSelectionContentDescription(context: Context): String {
        val res = context.resources
        if (selectedStartItem == null && selectedEndItem == null) {
            return res.getString(R.string.mtrl_picker_announce_current_selection_none)
        }
        val startPh = if (selectedStartItem == null)
            res.getString(R.string.mtrl_picker_announce_current_selection_none)
        else
            jalaliFormatLongMonth(selectedStartItem!!)
        val endPh = if (selectedEndItem == null)
            res.getString(R.string.mtrl_picker_announce_current_selection_none)
        else
            jalaliFormatLongMonth(selectedEndItem!!)

        return res.getString(R.string.mtrl_picker_announce_current_range_selection, startPh, endPh)
    }

    override fun getError(): String? {
        return if (TextUtils.isEmpty(error)) null else error.toString()
    }

    override fun getDefaultTitleResId(): Int {
        return R.string.mtrl_picker_range_header_title
    }

    override fun setTextInputFormat(format: SimpleDateFormat?) {
    }

    override fun onCreateTextInputView(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        bundle: Bundle?,
        constraints: CalendarConstraints,
        listener: OnSelectionChangedListener<Pair<Long?, Long?>?>
    ): View {
        val root = layoutInflater.inflate(R.layout.picker_text_input_date_range, parent, false)

        val startLayout =
            root.findViewById<TextInputLayout>(R.id.mtrl_picker_text_input_range_start)
        val endLayout = root.findViewById<TextInputLayout>(R.id.mtrl_picker_text_input_range_end)
        val startEdit = startLayout.getEditText()
        val endEdit = endLayout.getEditText()

        invalidRangeStartError = root.resources.getString(R.string.mtrl_picker_invalid_range)

        val formatHint = defaultTextInputHint
        startLayout.setPlaceholderText(formatHint)
        endLayout.setPlaceholderText(formatHint)

        if (selectedStartItem != null) {
            startEdit!!.setText(jalaliFormatSlash(selectedStartItem!!))
            proposedTextStart = selectedStartItem
        }
        if (selectedEndItem != null) {
            endEdit!!.setText(jalaliFormatSlash(selectedEndItem!!))
            proposedTextEnd = selectedEndItem
        }

        startEdit!!.addTextChangedListener(object :
            DateFormatTextWatcher(formatHint, startLayout, constraints) {
            override fun onValidDate(day: Long?) {
                proposedTextStart = day
                updateIfValidTextProposal(startLayout, endLayout, listener)
            }

            override fun onInvalidDate() {
                proposedTextStart = null
                updateIfValidTextProposal(startLayout, endLayout, listener)
            }
        })

        endEdit!!.addTextChangedListener(object :
            DateFormatTextWatcher(formatHint, endLayout, constraints) {
            override fun onValidDate(day: Long?) {
                proposedTextEnd = day
                updateIfValidTextProposal(startLayout, endLayout, listener)
            }

            override fun onInvalidDate() {
                proposedTextEnd = null
                updateIfValidTextProposal(startLayout, endLayout, listener)
            }
        })

        // Show keyboard
        //DateSelector.showKeyboardWithAutoHideBehavior(startEdit, endEdit)

        return root
    }

    private fun isValidRange(start: Long, end: Long): Boolean {
        return (start <= end)
    }

    private fun updateIfValidTextProposal(
        startInput: TextInputLayout,
        endInput: TextInputLayout,
        listener: OnSelectionChangedListener<Pair<Long?, Long?>?>
    ) {
        if (proposedTextStart == null || proposedTextEnd == null) {
            clearInvalidRange(startInput, endInput)
            listener.onIncompleteSelectionChanged()
        } else if (isValidRange(proposedTextStart!!, proposedTextEnd!!)) {
            selectedStartItem = proposedTextStart
            selectedEndItem = proposedTextEnd
            listener.onSelectionChanged(selection)
        } else {
            setInvalidRange(startInput, endInput)
            listener.onIncompleteSelectionChanged()
        }
        updateError(startInput, endInput)
    }

    private fun updateError(start: TextInputLayout, end: TextInputLayout) {
        error = if (!TextUtils.isEmpty(start.error)) {
            start.error
        } else if (!TextUtils.isEmpty(end.error)) {
            end.error
        } else {
            null
        }
    }

    private fun clearInvalidRange(start: TextInputLayout, end: TextInputLayout) {
        if (start.error != null && invalidRangeStartError.contentEquals(start.error)) {
            start.setError(null)
        }
        if (end.error != null && invalidRangeEndError.contentEquals(end.error)) {
            end.setError(null)
        }
    }

    private fun setInvalidRange(start: TextInputLayout, end: TextInputLayout) {
        start.setError(invalidRangeStartError)
        end.setError(invalidRangeEndError)
    }

    private fun jalaliFormatSlash(millis: Long): String {
        val pd = PersianDate(millis)
        return pd.shYear.toString() + "/" + pad2(pd.shMonth) + "/" + pad2(pd.shDay)
    }

    private fun jalaliFormatLongMonth(millis: Long): String {
        val pd = PersianDate(millis)
        return pd.shDay.toString() + " " + pd.monthName() + " " + pd.shYear
    }

    private fun pad2(v: Int): String {
        return if (v < 10) ("0$v") else v.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(selectedStartItem)
        dest.writeValue(selectedEndItem)
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<RangeDateSelector?> =
            object : Parcelable.Creator<RangeDateSelector?> {
                override fun createFromParcel(source: Parcel): RangeDateSelector {
                    val sel = RangeDateSelector()
                    sel.selectedStartItem =
                        source.readValue(Long::class.java.classLoader) as Long?
                    sel.selectedEndItem =
                        source.readValue(Long::class.java.classLoader) as Long?
                    return sel
                }

                override fun newArray(size: Int): Array<RangeDateSelector?> {
                    return arrayOfNulls<RangeDateSelector>(size)
                }
            }
    }
}
