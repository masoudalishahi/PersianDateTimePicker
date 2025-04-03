package com.masoud.persiandatetimepicker.datepicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.RippleDrawable
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.util.Preconditions
import androidx.core.view.ViewCompat
import com.google.android.material.resources.MaterialResources
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.masoud.persiandatetimepicker.R

/**
 * Loads and applies `R.styleable.MaterialCalendarDay` attributes to [TextView]
 * instances.
 */
@SuppressLint("RestrictedApi")
internal class CalendarItemStyle private constructor(
    backgroundColor: ColorStateList?,
    textColor: ColorStateList,
    strokeColor: ColorStateList?,
    strokeWidth: Int,
    itemShape: ShapeAppearanceModel,
    insets: Rect
) {
    /**
     * The inset between the TextView horizontal edge - bounding the touch target for an item - and
     * the selection marker.
     *
     *
     * The selection marker's size is defined by the []
     */
    private val insets: Rect

    private val textColor: ColorStateList
    private val backgroundColor: ColorStateList?
    private val strokeColor: ColorStateList?
    private val strokeWidth: Int
    private val itemShape: ShapeAppearanceModel

    init {
        Preconditions.checkArgumentNonnegative(insets.left)
        Preconditions.checkArgumentNonnegative(insets.top)
        Preconditions.checkArgumentNonnegative(insets.right)
        Preconditions.checkArgumentNonnegative(insets.bottom)

        this.insets = insets
        this.textColor = textColor
        this.backgroundColor = backgroundColor
        this.strokeColor = strokeColor
        this.strokeWidth = strokeWidth
        this.itemShape = itemShape
    }

    /**
     * Applies the `R.styleable.MaterialCalendarDay` style to the provided `item`,
     * factoring in the `backgroundColorOverride` if not null.
     */
    /**
     * Applies the `R.styleable.MaterialCalendarDay` style to the provided `item`, with no
     * `backgroundColorOverride`.
     */
    @JvmOverloads
    fun styleItem(
        item: TextView,
        backgroundColorOverride: ColorStateList? = null,
        textColorOverride: ColorStateList? = null
    ) {
        val backgroundDrawable = MaterialShapeDrawable()
        val shapeMask = MaterialShapeDrawable()
        backgroundDrawable.setShapeAppearanceModel(itemShape)
        shapeMask.setShapeAppearanceModel(itemShape)
        backgroundDrawable.fillColor = backgroundColorOverride ?: backgroundColor
        backgroundDrawable.setStroke(strokeWidth.toFloat(), strokeColor)
        item.setTextColor(textColorOverride ?: textColor)
        val d = RippleDrawable(textColor.withAlpha(30), backgroundDrawable, shapeMask)
        ViewCompat.setBackground(
            item, InsetDrawable(d, insets.left, insets.top, insets.right, insets.bottom)
        )
    }

    val leftInset: Int
        get() = insets.left

    val rightInset: Int
        get() = insets.right

    val topInset: Int
        get() = insets.top

    val bottomInset: Int
        get() = insets.bottom

    companion object {
        /**
         * Creates a [CalendarItemStyle] using the provided [ ][R.styleable.MaterialCalendarItem].
         */
        @JvmStatic
        @SuppressLint("PrivateResource")
        fun create(
            context: Context, @StyleRes materialCalendarItemStyle: Int
        ): CalendarItemStyle {
            Preconditions.checkArgument(
                materialCalendarItemStyle != 0,
                "Cannot create a CalendarItemStyle with a styleResId of 0"
            )

            val styleableArray =
                context.obtainStyledAttributes(
                    materialCalendarItemStyle,
                    R.styleable.MaterialCalendarItem
                )
            val insetLeft =
                styleableArray.getDimensionPixelOffset(
                    R.styleable.MaterialCalendarItem_android_insetLeft, 0
                )
            val insetTop =
                styleableArray.getDimensionPixelOffset(
                    R.styleable.MaterialCalendarItem_android_insetTop, 0
                )
            val insetRight =
                styleableArray.getDimensionPixelOffset(
                    R.styleable.MaterialCalendarItem_android_insetRight, 0
                )
            val insetBottom =
                styleableArray.getDimensionPixelOffset(
                    R.styleable.MaterialCalendarItem_android_insetBottom, 0
                )
            val insets = Rect(insetLeft, insetTop, insetRight, insetBottom)

            val backgroundColor =
                MaterialResources.getColorStateList(
                    context, styleableArray, R.styleable.MaterialCalendarItem_itemFillColor
                )
            val textColor =
                MaterialResources.getColorStateList(
                    context, styleableArray, R.styleable.MaterialCalendarItem_itemTextColor
                )
            val strokeColor =
                MaterialResources.getColorStateList(
                    context, styleableArray, R.styleable.MaterialCalendarItem_itemStrokeColor
                )
            val strokeWidth =
                styleableArray.getDimensionPixelSize(
                    R.styleable.MaterialCalendarItem_itemStrokeWidth,
                    0
                )

            val shapeAppearanceResId =
                styleableArray.getResourceId(
                    R.styleable.MaterialCalendarItem_itemShapeAppearance,
                    0
                )
            val shapeAppearanceOverlayResId =
                styleableArray.getResourceId(
                    R.styleable.MaterialCalendarItem_itemShapeAppearanceOverlay, 0
                )

            val itemShape =
                ShapeAppearanceModel.builder(
                    context,
                    shapeAppearanceResId,
                    shapeAppearanceOverlayResId
                )
                    .build()

            styleableArray.recycle()

            return CalendarItemStyle(
                backgroundColor, textColor!!, strokeColor, strokeWidth, itemShape, insets
            )
        }
    }
}