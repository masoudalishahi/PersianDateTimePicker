package com.masoud.persiandatetimepicker.datepicker;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/**
 * Represents a single month in a Persian sense. 0-based months: 0=Farvardin, 11=Esfand.
 */
final class Month implements Comparable<Month>, Parcelable {

  // 12 months in persian
  public static final int FARVARDIN   = 0;
  public static final int ORDIBEHESHT = 1;
  public static final int KHORDAD     = 2;
  public static final int TIR         = 3;
  public static final int MORDAD      = 4;
  public static final int SHAHRIVAR   = 5;
  public static final int MEHR        = 6;
  public static final int ABAN        = 7;
  public static final int AZAR        = 8;
  public static final int DEY         = 9;
  public static final int BAHMAN      = 10;
  public static final int ESFAND      = 11;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({
          FARVARDIN, ORDIBEHESHT, KHORDAD, TIR, MORDAD, SHAHRIVAR, MEHR, ABAN, AZAR, DEY, BAHMAN, ESFAND
  })
  @interface Months {
  }

  @NonNull
  private final PersianCalendarHelper firstOfMonth;
  @Months
  final int month;
  final int year;
  final int daysInWeek;
  final int daysInMonth;
  final long timeInMillis;

  @Nullable
  private String longName;

  private Month(@NonNull PersianCalendarHelper raw) {
    // Move day_of_month to 1
    raw.set(PersianCalendarHelper.DAY_OF_MONTH, 1);
    this.firstOfMonth = UtcDates.getDayCopy(raw);
    this.month = firstOfMonth.get(PersianCalendarHelper.MONTH);
    this.year = firstOfMonth.get(PersianCalendarHelper.YEAR);
    this.daysInWeek = firstOfMonth.getMaximum(PersianCalendarHelper.DAY_OF_WEEK);
    this.daysInMonth = firstOfMonth.getActualMaximum(PersianCalendarHelper.DAY_OF_MONTH);
    this.timeInMillis = firstOfMonth.getTimeInMillis();
  }

  static Month create(long timeInMillis) {
    PersianCalendarHelper helper = UtcDates.getUtcCalendar();
    helper.setTimeInMillis(timeInMillis);
    return new Month(helper);
  }

  static Month create(int year, @Months int month) {
    PersianCalendarHelper helper = UtcDates.getUtcCalendar();
    helper.set(PersianCalendarHelper.YEAR, year);
    helper.set(PersianCalendarHelper.MONTH, month);
    return new Month(helper);
  }

  static Month current() {
    return new Month(UtcDates.getTodayCalendar());
  }

  int daysFromStartOfWeekToFirstOfMonth(int firstDayOfWeek) {
    // Similar logic: day_of_week of day1 minus firstDayOfWeek
    int difference = firstOfMonth.get(PersianCalendarHelper.DAY_OF_WEEK) - firstDayOfWeek;
    if (difference < 0) {
      difference += daysInWeek;
    }
    return difference;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Month that)) return false;
    return (this.month == that.month && this.year == that.year);
  }

  @Override
  public int hashCode() {
    Object[] hashedFields = {month, year};
    return Arrays.hashCode(hashedFields);
  }

  @Override
  public int compareTo(@NonNull Month other) {
    return Long.compare(timeInMillis, other.timeInMillis);
  }

  /**
   * Returns difference in months between this and other. Could be negative.
   */
  int monthsUntil(@NonNull Month other) {
    return (other.year - year) * 12 + (other.month - month);
  }

  long getStableId() {
    return firstOfMonth.getTimeInMillis();
  }

  long getDay(int day) {
    PersianCalendarHelper c = UtcDates.getDayCopy(firstOfMonth);
    c.set(PersianCalendarHelper.DAY_OF_MONTH, day);
    return c.getTimeInMillis();
  }

  int getDayOfMonth(long date) {
    PersianCalendarHelper c = UtcDates.getDayCopy(firstOfMonth);
    c.setTimeInMillis(date);
    return c.get(PersianCalendarHelper.DAY_OF_MONTH);
  }

  Month monthsLater(int months) {
    PersianCalendarHelper cal = UtcDates.getDayCopy(firstOfMonth);
    cal.add(PersianCalendarHelper.MONTH, months);
    return new Month(cal);
  }

  String getLongName() {
    if (longName == null) {
      longName = DateStrings.getYearMonth(firstOfMonth.getTimeInMillis());
    }
    return longName;
  }

  public static final Creator<Month> CREATOR = new Creator<>() {
    @Override
    public Month createFromParcel(Parcel source) {
      int year = source.readInt();
      int mon = source.readInt();
      return Month.create(year, mon);
    }

    @Override
    public Month[] newArray(int size) {
      return new Month[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    dest.writeInt(year);
    dest.writeInt(month);
  }
}
