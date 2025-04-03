package com.masoud.persiandatetimepicker.datepicker

import androidx.fragment.app.Fragment

internal abstract class PickerFragment<S> : Fragment() {
    @JvmField
    protected val onSelectionChangedListeners: LinkedHashSet<OnSelectionChangedListener<S?>?> =
        LinkedHashSet<OnSelectionChangedListener<S?>?>()

    abstract val dateSelector: DateSelector<S?>?

    /**
     * Adds a listener for selection changes.
     */
    fun addOnSelectionChangedListener(listener: OnSelectionChangedListener<S?>?) {
        onSelectionChangedListeners.add(listener)
    }

    /** Removes a listener for selection changes.  */
    fun removeOnSelectionChangedListener(listener: OnSelectionChangedListener<S?>?): Boolean {
        return onSelectionChangedListeners.remove(listener)
    }

    /** Removes all listeners for selection changes.  */
    fun clearOnSelectionChangedListeners() {
        onSelectionChangedListeners.clear()
    }
}
