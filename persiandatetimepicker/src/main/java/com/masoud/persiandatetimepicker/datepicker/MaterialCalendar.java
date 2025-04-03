package com.masoud.persiandatetimepicker.datepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pair;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.masoud.persiandatetimepicker.R;

import java.util.List;

/**
 * Fragment for days-of-week calendar with a grid of days.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class MaterialCalendar<S> extends PickerFragment<S> {

  enum CalendarSelector {
    DAY,
    YEAR
  }

  private static final String THEME_RES_ID_KEY = "THEME_RES_ID_KEY";
  private static final String GRID_SELECTOR_KEY = "GRID_SELECTOR_KEY";
  private static final String CALENDAR_CONSTRAINTS_KEY = "CALENDAR_CONSTRAINTS_KEY";
  private static final String DAY_VIEW_DECORATOR_KEY = "DAY_VIEW_DECORATOR_KEY";
  private static final String CURRENT_MONTH_KEY = "CURRENT_MONTH_KEY";
  private static final int SMOOTH_SCROLL_MAX = 3;

  @VisibleForTesting
  static final Object MONTHS_VIEW_GROUP_TAG = "MONTHS_VIEW_GROUP_TAG";
  @VisibleForTesting
  static final Object NAVIGATION_PREV_TAG = "NAVIGATION_PREV_TAG";
  @VisibleForTesting
  static final Object NAVIGATION_NEXT_TAG = "NAVIGATION_NEXT_TAG";
  @VisibleForTesting
  static final Object SELECTOR_TOGGLE_TAG = "SELECTOR_TOGGLE_TAG";

  @StyleRes
  private int themeResId;
  @Nullable
  private DateSelector<S> dateSelector;
  @Nullable
  private CalendarConstraints calendarConstraints;
  @Nullable
  private DayViewDecorator dayViewDecorator;
  @Nullable
  private Month current;
  private CalendarSelector calendarSelector;
  private CalendarStyle calendarStyle;
  private RecyclerView yearSelector;
  private RecyclerView recyclerView;
  private View monthPrev;
  private View monthNext;
  private View yearFrame;
  private View dayFrame;

  @NonNull
  public static <T> MaterialCalendar<T> newInstance(
          @NonNull DateSelector<T> dateSelector,
          @StyleRes int themeResId,
          @NonNull CalendarConstraints constraints) {
    return newInstance(dateSelector, themeResId, constraints, null);
  }

  @NonNull
  public static <T> MaterialCalendar<T> newInstance(
          @NonNull DateSelector<T> dateSelector,
          @StyleRes int themeResId,
          @NonNull CalendarConstraints constraints,
          @Nullable DayViewDecorator dayViewDecorator) {
    MaterialCalendar<T> mc = new MaterialCalendar<>();
    Bundle args = new Bundle();
    args.putInt(THEME_RES_ID_KEY, themeResId);
    args.putParcelable(GRID_SELECTOR_KEY, dateSelector);
    args.putParcelable(CALENDAR_CONSTRAINTS_KEY, constraints);
    args.putParcelable(DAY_VIEW_DECORATOR_KEY, dayViewDecorator);
    args.putParcelable(CURRENT_MONTH_KEY, constraints.getOpenAt());
    mc.setArguments(args);
    return mc;
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle out) {
    super.onSaveInstanceState(out);
    out.putInt(THEME_RES_ID_KEY, themeResId);
    out.putParcelable(GRID_SELECTOR_KEY, dateSelector);
    out.putParcelable(CALENDAR_CONSTRAINTS_KEY, calendarConstraints);
    out.putParcelable(DAY_VIEW_DECORATOR_KEY, dayViewDecorator);
    out.putParcelable(CURRENT_MONTH_KEY, current);
  }

  @Override
  public void onCreate(@Nullable Bundle bundle) {
    super.onCreate(bundle);
    Bundle b = (bundle == null ? getArguments() : bundle);
    themeResId = b.getInt(THEME_RES_ID_KEY);
    dateSelector = b.getParcelable(GRID_SELECTOR_KEY);
    calendarConstraints = b.getParcelable(CALENDAR_CONSTRAINTS_KEY);
    dayViewDecorator = b.getParcelable(DAY_VIEW_DECORATOR_KEY);
    current = b.getParcelable(CURRENT_MONTH_KEY);
  }

  @NonNull
  @Override
  public View onCreateView(@NonNull LayoutInflater li, @Nullable ViewGroup vg, @Nullable Bundle st) {
    ContextThemeWrapper themedContext = new ContextThemeWrapper(getContext(), themeResId);
    calendarStyle = new CalendarStyle(themedContext);
    LayoutInflater inflater = li.cloneInContext(themedContext);

    Month earliest = calendarConstraints.getStart();
    int layout;
    final int orientation;
    if (MaterialDatePicker.isFullscreen(themedContext)) {
      layout = R.layout.calendar_vertical;
      orientation = LinearLayoutManager.VERTICAL;
    } else {
      layout = R.layout.calendar_horizontal;
      orientation = LinearLayoutManager.HORIZONTAL;
    }

    View root = inflater.inflate(layout, vg, false);
    //root.setMinimumHeight(getDialogPickerHeight(requireContext()));
    GridView daysHeader = root.findViewById(R.id.mtrl_calendar_days_of_week);

    ViewCompat.setAccessibilityDelegate(daysHeader, new AccessibilityDelegateCompat() {
      @Override
      public void onInitializeAccessibilityNodeInfo(
              @NonNull View v, @NonNull AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(v, info);
        // remove row/col
        info.setCollectionInfo(null);
      }
    });

    int firstDayOfWeek = calendarConstraints.getFirstDayOfWeek();
    if (firstDayOfWeek > 0) {
      daysHeader.setAdapter(new DaysOfWeekAdapter(firstDayOfWeek));
    } else {
      daysHeader.setAdapter(new DaysOfWeekAdapter());
    }
    daysHeader.setNumColumns(earliest.daysInWeek);
    daysHeader.setEnabled(false);

    recyclerView = root.findViewById(R.id.mtrl_calendar_months);
    SmoothCalendarLayoutManager lm = new SmoothCalendarLayoutManager(
            getContext(), orientation, false) {
      @Override
      protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state,
                                               @NonNull int[] extraSpace) {
        extraSpace[0] = 0;
        extraSpace[1] = 0;
      }
    };
    recyclerView.setLayoutManager(lm);
    recyclerView.setTag(MONTHS_VIEW_GROUP_TAG);

    @SuppressLint("NotifyDataSetChanged") final MonthsPagerAdapter monthsAdapter = new MonthsPagerAdapter(
            themedContext,
            dateSelector,
            calendarConstraints,
            dayViewDecorator,
            day -> {
              if (calendarConstraints.getDateValidator().isValid(day)) {
                dateSelector.select(day);
                for (OnSelectionChangedListener<S> l : onSelectionChangedListeners) {
                  l.onSelectionChanged(dateSelector.getSelection());
                }
                recyclerView.getAdapter().notifyDataSetChanged();

                if (yearSelector != null) {
                  yearSelector.getAdapter().notifyDataSetChanged();
                }
              }
            }
    );

    recyclerView.setAdapter(monthsAdapter);

    int columns = themedContext.getResources().getInteger(R.integer.mtrl_calendar_year_selector_span);
    yearSelector = root.findViewById(R.id.mtrl_calendar_year_selector_frame);
    if (yearSelector != null) {
      yearSelector.setHasFixedSize(true);
      yearSelector.setLayoutManager(new GridLayoutManager(themedContext, columns, RecyclerView.VERTICAL, false));
      yearSelector.setAdapter(new YearGridAdapter(this));
      yearSelector.addItemDecoration(createItemDecoration());
    }

    if (root.findViewById(R.id.month_navigation_fragment_toggle) != null) {
      addActionsToMonthNavigation(root, monthsAdapter);
    }

    if (!MaterialDatePicker.isFullscreen(themedContext)) {
      new PagerSnapHelper().attachToRecyclerView(recyclerView);
    }
    recyclerView.scrollToPosition(monthsAdapter.getPosition(current));
    setUpForAccessibility();
    return root;
  }

  private void setUpForAccessibility() {
    ViewCompat.setAccessibilityDelegate(recyclerView, new AccessibilityDelegateCompat() {
      @Override
      public void onInitializeAccessibilityNodeInfo(@NonNull View v, @NonNull AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(v, info);
        info.setScrollable(false);
      }
    });
  }

  @NonNull
  private RecyclerView.ItemDecoration createItemDecoration() {
    return new RecyclerView.ItemDecoration() {
      private final PersianCalendarHelper startItem = UtcDates.getUtcCalendar();
      private final PersianCalendarHelper endItem = UtcDates.getUtcCalendar();

      @Override
      public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView rv, @NonNull RecyclerView.State state) {
        if (!(rv.getAdapter() instanceof YearGridAdapter adapter) ||
                !(rv.getLayoutManager() instanceof GridLayoutManager layoutManager)) {
          return;
        }

          List<Pair<Long, Long>> ranges = (List<Pair<Long, Long>>) dateSelector.getSelectedRanges();
        for (Pair<Long, Long> range : ranges) {
          if (range.first == null || range.second == null) {
            continue;
          }
          startItem.setTimeInMillis(range.first);
          endItem.setTimeInMillis(range.second);

          int startYear = startItem.get(PersianCalendarHelper.YEAR);
          int endYear = endItem.get(PersianCalendarHelper.YEAR);

          int firstPos = adapter.getPositionForYear(startYear);
          int lastPos = adapter.getPositionForYear(endYear);

          View firstView = layoutManager.findViewByPosition(firstPos);
          View lastView = layoutManager.findViewByPosition(lastPos);

          int firstRow = firstPos / layoutManager.getSpanCount();
          int lastRow = lastPos / layoutManager.getSpanCount();

          for (int row = firstRow; row <= lastRow; row++) {
            int firstPosInRow = row * layoutManager.getSpanCount();
            View rowView = layoutManager.findViewByPosition(firstPosInRow);
            if (rowView == null) continue;
            int top = rowView.getTop() + calendarStyle.year.getTopInset();
            int bottom = rowView.getBottom() - calendarStyle.year.getBottomInset();
            int left = (row == firstRow && firstView != null)
                    ? firstView.getLeft() + firstView.getWidth() / 2 : 0;
            int right = (row == lastRow && lastView != null)
                    ? lastView.getLeft() + lastView.getWidth() / 2 : rv.getWidth();
            canvas.drawRect(left, top, right, bottom, calendarStyle.rangeFill);
          }
        }
      }
    };
  }

  @Nullable
  Month getCurrentMonth() {
    return current;
  }

  @Nullable
  CalendarConstraints getCalendarConstraints() {
    return calendarConstraints;
  }

  void setCurrentMonth(Month moveTo) {
    MonthsPagerAdapter adapter = (MonthsPagerAdapter) recyclerView.getAdapter();
    int moveToPosition = adapter.getPosition(moveTo);
    int distance = moveToPosition - adapter.getPosition(current);
    boolean jump = Math.abs(distance) > SMOOTH_SCROLL_MAX;
    boolean forward = distance > 0;
    current = moveTo;
    if (jump && forward) {
      recyclerView.scrollToPosition(moveToPosition - SMOOTH_SCROLL_MAX);
      postSmoothRecyclerViewScroll(moveToPosition);
    } else if (jump) {
      recyclerView.scrollToPosition(moveToPosition + SMOOTH_SCROLL_MAX);
      postSmoothRecyclerViewScroll(moveToPosition);
    } else {
      postSmoothRecyclerViewScroll(moveToPosition);
    }
  }

  @Nullable
  @Override
  public DateSelector<S> getDateSelector() {
    return dateSelector;
  }

  CalendarStyle getCalendarStyle() {
    return calendarStyle;
  }

  interface OnDayClickListener {
    void onDayClick(long day);
  }

  @Px
  static int getDayHeight(@NonNull Context context) {
    return context.getResources().getDimensionPixelSize(R.dimen.mtrl_calendar_day_height);
  }

  void setSelector(CalendarSelector sel) {
    this.calendarSelector = sel;
    if (sel == CalendarSelector.YEAR) {
      yearSelector.getLayoutManager().scrollToPosition(
              ((YearGridAdapter)yearSelector.getAdapter()).getPositionForYear(current.year));
      yearFrame.setVisibility(View.VISIBLE);
      dayFrame.setVisibility(View.GONE);
      monthPrev.setVisibility(View.GONE);
      monthNext.setVisibility(View.GONE);
    } else {
      yearFrame.setVisibility(View.GONE);
      dayFrame.setVisibility(View.VISIBLE);
      monthPrev.setVisibility(View.VISIBLE);
      monthNext.setVisibility(View.VISIBLE);
      setCurrentMonth(current);
    }
  }

  void toggleVisibleSelector() {
    if (calendarSelector == CalendarSelector.YEAR) {
      setSelector(CalendarSelector.DAY);
    } else {
      setSelector(CalendarSelector.YEAR);
    }
  }

  private void addActionsToMonthNavigation(@NonNull final View root,
                                           @NonNull final MonthsPagerAdapter adapter) {
    final MaterialButton monthDropSelect = root.findViewById(R.id.month_navigation_fragment_toggle);
    monthDropSelect.setTag(SELECTOR_TOGGLE_TAG);

    ViewCompat.setAccessibilityDelegate(monthDropSelect, new AccessibilityDelegateCompat() {
      @Override
      public void onInitializeAccessibilityNodeInfo(@NonNull View v, @NonNull AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(v, info);
        info.setHintText(
                dayFrame.getVisibility() == View.VISIBLE
                        ? getString(R.string.mtrl_picker_toggle_to_year_selection)
                        : getString(R.string.mtrl_picker_toggle_to_day_selection)
        );
      }
    });

    monthPrev = root.findViewById(R.id.month_navigation_previous);
    monthPrev.setTag(NAVIGATION_PREV_TAG);
    monthNext = root.findViewById(R.id.month_navigation_next);
    monthNext.setTag(NAVIGATION_NEXT_TAG);

    yearFrame = root.findViewById(R.id.mtrl_calendar_year_selector_frame);
    dayFrame = root.findViewById(R.id.mtrl_calendar_day_selector_frame);
    setSelector(CalendarSelector.DAY);
    monthDropSelect.setText(current.getLongName());
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
      @Override
      public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
        int currentItem;
        if (dx < 0) {
          currentItem = getLayoutManager().findFirstVisibleItemPosition();
        } else {
          currentItem = getLayoutManager().findLastVisibleItemPosition();
        }
        current = adapter.getPageMonth(currentItem);
        monthDropSelect.setText(adapter.getPageTitle(currentItem));
      }

      @Override
      public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          CharSequence announce = monthDropSelect.getText();
            rv.announceForAccessibility(announce);
        }
      }
    });

    monthDropSelect.setOnClickListener(v -> toggleVisibleSelector());

    monthNext.setOnClickListener(v -> {
      int currentItem = getLayoutManager().findFirstVisibleItemPosition();
      if (currentItem+1 < recyclerView.getAdapter().getItemCount()) {
        setCurrentMonth(adapter.getPageMonth(currentItem+1));
      }
    });

    monthPrev.setOnClickListener(v -> {
      int currentItem = getLayoutManager().findLastVisibleItemPosition();
      if (currentItem-1 >= 0) {
        setCurrentMonth(adapter.getPageMonth(currentItem-1));
      }
    });
  }

  private void postSmoothRecyclerViewScroll(final int position) {
    recyclerView.post(() -> recyclerView.smoothScrollToPosition(position));
  }

  private static int getDialogPickerHeight(@NonNull Context ctx) {
    Resources r = ctx.getResources();
    int navHeight = r.getDimensionPixelSize(R.dimen.mtrl_calendar_navigation_height)
            + r.getDimensionPixelOffset(R.dimen.mtrl_calendar_navigation_top_padding)
            + r.getDimensionPixelOffset(R.dimen.mtrl_calendar_navigation_bottom_padding);
    int daysOfWeekHeight = r.getDimensionPixelSize(R.dimen.mtrl_calendar_days_of_week_height);
    int calendarHeight = MonthAdapter.MAXIMUM_WEEKS
            * r.getDimensionPixelSize(R.dimen.mtrl_calendar_day_height)
            + (MonthAdapter.MAXIMUM_WEEKS - 1)
            * r.getDimensionPixelOffset(R.dimen.mtrl_calendar_month_vertical_padding);
    int bottomPadding = r.getDimensionPixelOffset(R.dimen.mtrl_calendar_bottom_padding);
    return navHeight + daysOfWeekHeight + calendarHeight + bottomPadding;
  }

  @NonNull
  LinearLayoutManager getLayoutManager() {
    return (LinearLayoutManager) recyclerView.getLayoutManager();
  }
}
