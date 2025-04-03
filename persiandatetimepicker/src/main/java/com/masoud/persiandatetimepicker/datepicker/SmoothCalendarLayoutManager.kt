package com.masoud.persiandatetimepicker.datepicker

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * Layout manager for [MaterialCalendar] that slows the scroll down to appear smoother for
 * months.
 */
internal open class SmoothCalendarLayoutManager(
    context: Context?,
    orientation: Int,
    reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView, state: RecyclerView.State?, position: Int
    ) {
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                }
            }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    companion object {
        /** Default value in [LinearSmoothScroller] is 25f  */
        private const val MILLISECONDS_PER_INCH = 100f
    }
}
