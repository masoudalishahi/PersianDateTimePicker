package com.masoud.persiandatetimepicker.datepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ViewUtils;
import androidx.core.util.Pair;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.masoud.persiandatetimepicker.R;
import java.util.Calendar;

final class MaterialCalendarGridView extends GridView {

    private final PersianCalendarHelper dayCompute = UtcDates.getUtcCalendar();
    private final boolean nestedScrollable;

    public MaterialCalendarGridView(Context context) {
        this(context, null);
    }

    public MaterialCalendarGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialCalendarGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (MaterialDatePicker.isFullscreen(getContext())) {
            setNextFocusLeftId(R.id.cancel_button);
            setNextFocusRightId(R.id.confirm_button);
        }
        nestedScrollable = MaterialDatePicker.isNestedScrollable(getContext());
        ViewCompat.setAccessibilityDelegate(
                this,
                new AccessibilityDelegateCompat() {
                    @Override
                    public void onInitializeAccessibilityNodeInfo(
                            @NonNull View view, @NonNull AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                        accessibilityNodeInfoCompat.setCollectionInfo(null);
                    }
                });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(Math.max(position, getAdapter().firstPositionInMonth()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(keyCode, event);
        if (!result) return false;

        if (getSelectedItemPosition() == INVALID_POSITION
                || getSelectedItemPosition() >= getAdapter().firstPositionInMonth()) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            setSelection(getAdapter().firstPositionInMonth());
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public MonthAdapter getAdapter() {
        return (MonthAdapter) super.getAdapter();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof MonthAdapter)) {
            throw new IllegalArgumentException(
                    String.format("%1$s must have its Adapter set to a %2$s",
                            MaterialCalendarGridView.class.getCanonicalName(),
                            MonthAdapter.class.getCanonicalName()));
        }
        super.setAdapter(adapter);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        MonthAdapter monthAdapter = getAdapter();
        DateSelector<?> dateSelector = monthAdapter.dateSelector;
        CalendarStyle calendarStyle = monthAdapter.calendarStyle;

        int firstVisiblePositionInMonth =
                Math.max(monthAdapter.firstPositionInMonth(), getFirstVisiblePosition());
        int lastVisiblePositionInMonth =
                Math.min(monthAdapter.lastPositionInMonth(), getLastVisiblePosition());

        Long firstOfMonth = monthAdapter.getItem(firstVisiblePositionInMonth);
        Long lastOfMonth = monthAdapter.getItem(lastVisiblePositionInMonth);

        for (Pair<Long, Long> range : dateSelector.getSelectedRanges()) {
            if (range.first == null || range.second == null) continue;

            long startItem = range.first;
            long endItem = range.second;

            if (skipMonth(firstOfMonth, lastOfMonth, startItem, endItem)) continue;

            @SuppressLint("RestrictedApi") boolean isRtl = ViewUtils.isLayoutRtl(this);

            int firstHighlightPosition;
            int rangeHighlightStart;
            if (startItem < firstOfMonth) {
                firstHighlightPosition = firstVisiblePositionInMonth;
                rangeHighlightStart = monthAdapter.isFirstInRow(firstHighlightPosition) ? 0 :
                        (!isRtl ? getChildAtPosition(firstHighlightPosition - 1).getRight()
                                : getChildAtPosition(firstHighlightPosition - 1).getLeft());
            } else {
                dayCompute.setTimeInMillis(startItem);
                firstHighlightPosition = monthAdapter.dayToPosition(dayCompute.get(Calendar.DAY_OF_MONTH));
                rangeHighlightStart = horizontalMidPoint(getChildAtPosition(firstHighlightPosition));
            }

            int lastHighlightPosition;
            int rangeHighlightEnd;
            if (endItem > lastOfMonth) {
                lastHighlightPosition = lastVisiblePositionInMonth;
                rangeHighlightEnd = monthAdapter.isLastInRow(lastHighlightPosition) ? getWidth() :
                        (!isRtl ? getChildAtPosition(lastHighlightPosition).getRight()
                                : getChildAtPosition(lastHighlightPosition).getLeft());
            } else {
                dayCompute.setTimeInMillis(endItem);
                lastHighlightPosition = monthAdapter.dayToPosition(dayCompute.get(Calendar.DAY_OF_MONTH));
                rangeHighlightEnd = horizontalMidPoint(getChildAtPosition(lastHighlightPosition));
            }

            int firstRow = (int) monthAdapter.getItemId(firstHighlightPosition);
            int lastRow = (int) monthAdapter.getItemId(lastHighlightPosition);
            for (int row = firstRow; row <= lastRow; row++) {
                int firstPositionInRow = row * getNumColumns();
                int lastPositionInRow = firstPositionInRow + getNumColumns() - 1;
                View firstView = getChildAtPosition(firstPositionInRow);
                int top = firstView.getTop() + calendarStyle.day.getTopInset();
                int bottom = firstView.getBottom() - calendarStyle.day.getBottomInset();
                int left = !isRtl
                        ? (firstPositionInRow > firstHighlightPosition ? 0 : rangeHighlightStart)
                        : (lastHighlightPosition > lastPositionInRow ? 0 : rangeHighlightEnd);
                int right = !isRtl
                        ? (lastHighlightPosition > lastPositionInRow ? getWidth() : rangeHighlightEnd)
                        : (firstPositionInRow > firstHighlightPosition ? getWidth() : rangeHighlightStart);
                canvas.drawRect(left, top, right, bottom, calendarStyle.rangeFill);
            }
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*if (nestedScrollable) {
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }*/
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            gainFocus(direction, previouslyFocusedRect);
        } else {
            super.onFocusChanged(false, direction, previouslyFocusedRect);
        }
    }

    private void gainFocus(int direction, Rect previouslyFocusedRect) {
        if (direction == FOCUS_UP) {
            setSelection(getAdapter().lastPositionInMonth());
        } else if (direction == FOCUS_DOWN) {
            setSelection(getAdapter().firstPositionInMonth());
        } else {
            super.onFocusChanged(true, direction, previouslyFocusedRect);
        }
    }

    private View getChildAtPosition(int position) {
        return getChildAt(position - getFirstVisiblePosition());
    }

    private static boolean skipMonth(@NonNull Long firstOfMonth,
                                     @NonNull Long lastOfMonth,
                                     @NonNull Long startDay,
                                     @NonNull Long endDay) {
        return startDay > lastOfMonth || endDay < firstOfMonth;
    }

    private static int horizontalMidPoint(@NonNull View view) {
        return view.getLeft() + view.getWidth() / 2;
    }
}
