package com.masoud.persiandatetimepicker.datepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.android.material.resources.MaterialAttributes;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.masoud.persiandatetimepicker.R;
import saman.zamani.persiandate.PersianDate;

/**
 * A DateSelector that uses one Long for a single date selection (all Jalali).
 * The text field input uses "yyyy/MM/dd",
 * but the header display uses "dd ماه سال" format.
 */
public class SingleDateSelector implements DateSelector<Long> {

  @Nullable
  private CharSequence error;
  @Nullable
  private Long selectedItem;

  private final Map<Long, String> jalaliDateCache = new HashMap<>();

  @Override
  public void select(long selection) {
    selectedItem = selection;
  }

  private void clearSelection() {
    selectedItem = null;
  }

  @Override
  public void setSelection(@Nullable Long selection) {
    selectedItem = (selection == null)? null : UtcDates.canonicalYearMonthDay(selection);
  }

  @Override
  public boolean isSelectionComplete() {
    return (selectedItem != null);
  }

  @NonNull
  @Override
  public Collection<Pair<Long, Long>> getSelectedRanges() {
    return new ArrayList<>();
  }

  @NonNull
  @Override
  public Collection<Long> getSelectedDays() {
    ArrayList<Long> r = new ArrayList<>();
    if (selectedItem != null) {
      r.add(selectedItem);
    }
    return r;
  }

  @Override
  @Nullable
  public Long getSelection() {
    return selectedItem;
  }

  @Override
  public void setTextInputFormat(@Nullable java.text.SimpleDateFormat format) {
  }

  @NonNull
  @Override
  public View onCreateTextInputView(@NonNull LayoutInflater layoutInflater,
                                    @Nullable ViewGroup parent,
                                    @Nullable Bundle bundle,
                                    @NonNull CalendarConstraints constraints,
                                    @NonNull OnSelectionChangedListener<Long> listener) {

    View root = layoutInflater.inflate(R.layout.picker_text_input_date, parent, false);
    TextInputLayout inputLayout = root.findViewById(R.id.mtrl_picker_text_input_date);
    EditText editText = inputLayout.getEditText();

    String formatHint = UtcDates.getDefaultTextInputHint(); // مثلاً "yyyy/MM/dd"
    inputLayout.setPlaceholderText(formatHint);

    if (selectedItem != null) {
      editText.setText(jalaliFormatSlash(selectedItem));
    }

    editText.addTextChangedListener(new DateFormatTextWatcher(formatHint, inputLayout, constraints) {
      @Override
      public void onValidDate(@Nullable Long day) {
        if (day == null) {
          clearSelection();
        } else {
          select(day);
        }
        error = null;
        listener.onSelectionChanged(getSelection());
      }
      @Override
      public void onInvalidDate() {
        error = inputLayout.getError();
        listener.onIncompleteSelectionChanged();
      }
    });

    // Show keyboard if possible
 //   DateSelector.showKeyboardWithAutoHideBehavior(editText);

    return root;
  }

  @SuppressLint("RestrictedApi")
  @Override
  public int getDefaultThemeResId(Context context) {
    return MaterialAttributes.resolveOrThrow(
            context, R.attr.materialCalendarTheme, MaterialDatePicker.class.getCanonicalName());
  }

  @NonNull
  @Override
  public String getSelectionDisplayString(@NonNull Context context) {
    Resources res = context.getResources();
    if (selectedItem == null) {
      return res.getString(R.string.mtrl_picker_date_header_unselected);
    }
    String dateString = jalaliFormatLongMonth(selectedItem);
    return res.getString(R.string.mtrl_picker_date_header_selected, dateString);
  }

  @NonNull
  @Override
  public String getSelectionContentDescription(@NonNull Context context) {
    Resources res = context.getResources();
    if (selectedItem == null) {
      return res.getString(R.string.mtrl_picker_announce_current_selection_none);
    }
    String dateString = jalaliFormatLongMonth(selectedItem);
    return res.getString(R.string.mtrl_picker_announce_current_selection, dateString);
  }

  @Nullable
  @Override
  public String getError() {
    return TextUtils.isEmpty(error) ? null : error.toString();
  }

  @Override
  public int getDefaultTitleResId() {
    return R.string.mtrl_picker_date_header_title;
  }

  public static final Creator<SingleDateSelector> CREATOR = new Creator<>() {
    @Override
    public SingleDateSelector createFromParcel(@NonNull Parcel source) {
      SingleDateSelector sds = new SingleDateSelector();
      sds.selectedItem = (Long) source.readValue(Long.class.getClassLoader());
      return sds;
    }
    @Override
    public SingleDateSelector[] newArray(int size) {
      return new SingleDateSelector[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    dest.writeValue(selectedItem);
  }

  private String jalaliFormatSlash(long millis) {
    PersianDate pd = new PersianDate(millis);
    return pd.getShYear() + "/" + pad2(pd.getShMonth()) + "/" + pad2(pd.getShDay());
  }

  private String jalaliFormatLongMonth(long millis) {
    if (jalaliDateCache.containsKey(millis))
      return jalaliDateCache.get(millis);

    PersianDate pd = new PersianDate(millis);
    String formatted = pd.getShDay() + " " + pd.monthName() + " " + pd.getShYear();
    jalaliDateCache.put(millis, formatted);

    return formatted;
  }

  private String pad2(int v) {
    return (v < 10) ? "0" + v : String.valueOf(v);
  }
}
