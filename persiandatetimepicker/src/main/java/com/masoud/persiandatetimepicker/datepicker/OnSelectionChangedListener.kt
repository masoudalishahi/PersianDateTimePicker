package com.masoud.persiandatetimepicker.datepicker

import androidx.annotation.RestrictTo

/** Listener that provides selection.  */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
abstract class OnSelectionChangedListener<S> {
    /** Called with the current selection.  */
    abstract fun onSelectionChanged(selection: S)

    open fun onIncompleteSelectionChanged() {}
}
