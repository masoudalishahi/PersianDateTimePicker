package com.masoud.persiandatetimepicker.datepicker;

/** Listener that provides the current {@code MaterialCalendar<S>} selection. */
public interface MaterialPickerOnPositiveButtonClickListener<S> {

  /** Called with the current {@code MaterialCalendar<S>} selection. */
  void onPositiveButtonClick(S selection);
}
