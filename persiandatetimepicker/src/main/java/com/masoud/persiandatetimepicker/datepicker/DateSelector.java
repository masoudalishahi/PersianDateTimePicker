package com.masoud.persiandatetimepicker.datepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import java.text.SimpleDateFormat;
import java.util.Collection;

import androidx.core.util.Pair;

import com.google.android.material.internal.ViewUtils;

/**
 * Interface controlling how the calendar displays and returns selections.
 *
 * @param <S> The type of item for the final selection
 */
public interface DateSelector<S> extends Parcelable {

  @Nullable
  S getSelection();

  boolean isSelectionComplete();

  void setSelection(@NonNull S selection);

  void select(long selection);

  @NonNull
  Collection<Long> getSelectedDays();

  @NonNull
  Collection<Pair<Long, Long>> getSelectedRanges();

  @NonNull
  String getSelectionDisplayString(Context context);

  @NonNull
  String getSelectionContentDescription(@NonNull Context context);

  @Nullable
  String getError();

  @StringRes
  int getDefaultTitleResId();

  @StyleRes
  int getDefaultThemeResId(Context context);

  void setTextInputFormat(@Nullable SimpleDateFormat format);

  @NonNull
  View onCreateTextInputView(
          @NonNull LayoutInflater layoutInflater,
          @Nullable ViewGroup viewGroup,
          @Nullable Bundle bundle,
          @NonNull CalendarConstraints constraints,
          @NonNull OnSelectionChangedListener<S> listener);

  @SuppressLint("RestrictedApi")
  static void showKeyboardWithAutoHideBehavior(@NonNull EditText... editTexts) {
    if (editTexts.length == 0) {
      return;
    }

    View.OnFocusChangeListener listener =
            (view, hasFocus) -> {
              for (EditText editText : editTexts) {
                if (editText.hasFocus()) {
                  return;
                }
              }
              ViewUtils.hideKeyboard(view, /* useWindowInsetsController= */ false);
            };

    for (EditText editText : editTexts) {
      editText.setOnFocusChangeListener(listener);
    }

    // TODO(b/246354286): Investigate issue with keyboard not showing on Android 12+
    View viewToFocus = editTexts[0];
    viewToFocus.postDelayed(
            () ->
                    ViewUtils.requestFocusAndShowKeyboard(
                            viewToFocus),
            100);
  }

}
