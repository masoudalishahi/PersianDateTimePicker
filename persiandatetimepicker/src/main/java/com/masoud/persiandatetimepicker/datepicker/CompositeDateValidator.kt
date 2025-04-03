package com.masoud.persiandatetimepicker.datepicker

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.core.util.Preconditions
import com.masoud.persiandatetimepicker.datepicker.CalendarConstraints.DateValidator
import kotlin.jvm.java

/**
 * A [DateValidator] that accepts a list of Date Validators.
 */
class CompositeDateValidator private constructor(
    private val validators: List<DateValidator?>,
    private val operator: Operator
) : DateValidator {

    private interface Operator {
        fun isValid(validators: List<DateValidator?>, date: Long): Boolean
        val id: Int
    }

    override fun isValid(date: Long): Boolean {
        return operator.isValid(validators, date)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(validators)
        dest.writeInt(operator.id)
    }

    override fun describeContents(): Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompositeDateValidator) return false
        return validators == other.validators && operator.id == other.operator.id
    }

    override fun hashCode(): Int {
        return validators.hashCode()
    }

    companion object {
        private const val COMPARATOR_ANY_ID = 1
        private const val COMPARATOR_ALL_ID = 2

        private val ANY_OPERATOR = object : Operator {
            override fun isValid(validators: List<DateValidator?>, date: Long): Boolean {
                for (validator in validators) {
                    if (validator?.isValid(date) == true) {
                        return true
                    }
                }
                return false
            }

            override val id: Int
                get() = COMPARATOR_ANY_ID
        }

        private val ALL_OPERATOR = object : Operator {
            override fun isValid(validators: List<DateValidator?>, date: Long): Boolean {
                for (validator in validators) {
                    if (validator?.isValid(date) == false) {
                        return false
                    }
                }
                return true
            }

            override val id: Int
                get() = COMPARATOR_ALL_ID
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<CompositeDateValidator> {
            @SuppressLint("RestrictedApi")
            override fun createFromParcel(source: Parcel): CompositeDateValidator {
                @Suppress("UNCHECKED_CAST")
                val v = source.readArrayList(DateValidator::class.java.classLoader)
                        as List<DateValidator?>
                val id = source.readInt()
                val op = when (id) {
                    COMPARATOR_ALL_ID -> ALL_OPERATOR
                    COMPARATOR_ANY_ID -> ANY_OPERATOR
                    else -> ALL_OPERATOR
                }
                return CompositeDateValidator(Preconditions.checkNotNull(v), op)
            }

            override fun newArray(size: Int): Array<CompositeDateValidator?> {
                return arrayOfNulls(size)
            }
        }

        @JvmStatic
        fun allOf(validators: List<DateValidator?>): DateValidator {
            return CompositeDateValidator(validators, ALL_OPERATOR)
        }

        @JvmStatic
        fun anyOf(validators: List<DateValidator?>): DateValidator {
            return CompositeDateValidator(validators, ANY_OPERATOR)
        }
    }
}
