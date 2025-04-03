package com.masoud.persiandatetimepicker.datepicker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;
import java.util.Arrays;
import java.util.Objects;

/**
 * Used to limit display range of the calendar and set an openAt month.
 */
public final class CalendarConstraints implements Parcelable {

  @NonNull
  private final Month start;
  @NonNull
  private final Month end;
  @NonNull
  private final DateValidator validator;
  @Nullable
  private Month openAt;
  private final int firstDayOfWeek;
  private final int yearSpan;
  private final int monthSpan;

  public interface DateValidator extends Parcelable {
    boolean isValid(long date);
  }

  private CalendarConstraints(@NonNull Month start,
                              @NonNull Month end,
                              @NonNull DateValidator validator,
                              @Nullable Month openAt,
                              int firstDayOfWeek) {
    Objects.requireNonNull(start, "start cannot be null");
    Objects.requireNonNull(end, "end cannot be null");
    Objects.requireNonNull(validator, "validator cannot be null");
    this.start = start;
    this.end = end;
    this.validator = validator;
    this.openAt = openAt;
    this.firstDayOfWeek = firstDayOfWeek;
    if (openAt != null && start.compareTo(openAt) > 0) {
      throw new IllegalArgumentException("start Month cannot be after current Month");
    }
    if (openAt != null && openAt.compareTo(end) > 0) {
      throw new IllegalArgumentException("current Month cannot be after end Month");
    }
    if (firstDayOfWeek < 0 || firstDayOfWeek > 7) {
      throw new IllegalArgumentException("firstDayOfWeek is not valid");
    }
    this.monthSpan = start.monthsUntil(end) + 1;
    this.yearSpan = end.year - start.year + 1;
  }

  boolean isWithinBounds(long date) {
    return start.getDay(1) <= date && date <= end.getDay(end.daysInMonth);
  }

  public DateValidator getDateValidator() {
    return validator;
  }

  @NonNull
  Month getStart() {
    return start;
  }

  @NonNull
  Month getEnd() {
    return end;
  }

  @Nullable
  Month getOpenAt() {
    return openAt;
  }

  void setOpenAt(@Nullable Month openAt) {
    this.openAt = openAt;
  }

  int getFirstDayOfWeek() {
    return firstDayOfWeek;
  }

  int getMonthSpan() {
    return monthSpan;
  }

  int getYearSpan() {
    return yearSpan;
  }

  public long getStartMs() {
    return start.timeInMillis;
  }

  public long getEndMs() {
    return end.timeInMillis;
  }

  @Nullable
  public Long getOpenAtMs() {
    return openAt == null ? null : openAt.timeInMillis;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CalendarConstraints that)) return false;
    return start.equals(that.start)
            && end.equals(that.end)
            && ObjectsCompat.equals(openAt, that.openAt)
            && firstDayOfWeek == that.firstDayOfWeek
            && validator.equals(that.validator);
  }

  @Override
  public int hashCode() {
    Object[] fields = {start, end, openAt, firstDayOfWeek, validator};
    return Arrays.hashCode(fields);
  }

  public static final Creator<CalendarConstraints> CREATOR = new Creator<>() {
      @Override
      public CalendarConstraints createFromParcel(Parcel source) {
          Month start = source.readParcelable(Month.class.getClassLoader());
          Month end = source.readParcelable(Month.class.getClassLoader());
          Month open = source.readParcelable(Month.class.getClassLoader());
          DateValidator val = source.readParcelable(DateValidator.class.getClassLoader());
          int fdow = source.readInt();
          return new CalendarConstraints(start, end, val, open, fdow);
      }

      @Override
      public CalendarConstraints[] newArray(int size) {
          return new CalendarConstraints[size];
      }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(start, 0);
    dest.writeParcelable(end, 0);
    dest.writeParcelable(openAt, 0);
    dest.writeParcelable(validator, 0);
    dest.writeInt(firstDayOfWeek);
  }

  Month clamp(Month month) {
    if (month.compareTo(start) < 0) {
      return start;
    }
    if (month.compareTo(end) > 0) {
      return end;
    }
    return month;
  }

  /**
   * Builder
   */
  public static final class Builder {
    static final long DEFAULT_START = UtcDates.canonicalYearMonthDay(Month.create(1300, Month.FARVARDIN).timeInMillis);
    static final long DEFAULT_END   = UtcDates.canonicalYearMonthDay(Month.create(1500, Month.ESFAND).timeInMillis);

    private static final String DEEP_COPY_VALIDATOR_KEY = "DEEP_COPY_VALIDATOR_KEY";

    private long start = DEFAULT_START;
    private long end = DEFAULT_END;
    private Long openAt = null;
    private int firstDayOfWeek = 0;
    private DateValidator validator = DateValidatorPointForward.from(Long.MIN_VALUE);

    public Builder() {
    }

    Builder(@NonNull CalendarConstraints c) {
      start = c.start.timeInMillis;
      end   = c.end.timeInMillis;
      openAt = c.openAt == null ? null : c.openAt.timeInMillis;
      firstDayOfWeek = c.firstDayOfWeek;
      validator = c.validator;
    }

    @NonNull
    public Builder setStart(long ms) {
      start = ms;
      return this;
    }

    @NonNull
    public Builder setEnd(long ms) {
      end = ms;
      return this;
    }

    @NonNull
    public Builder setOpenAt(long ms) {
      openAt = ms;
      return this;
    }

    @NonNull
    public Builder setFirstDayOfWeek(int dow) {
      this.firstDayOfWeek = dow;
      return this;
    }

    @NonNull
    public Builder setValidator(@NonNull DateValidator val) {
      this.validator = val;
      return this;
    }

    @NonNull
    public CalendarConstraints build() {
      Month s = Month.create(start);
      Month e = Month.create(end);
      Month o = (openAt == null) ? null : Month.create(openAt);
      return new CalendarConstraints(s, e, validator, o, firstDayOfWeek);
    }
  }
}
