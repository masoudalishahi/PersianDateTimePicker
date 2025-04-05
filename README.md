# PersianDateTimePicker 📆🇮🇷

A modern, Material-based **Persian (Jalali) DateTime Picker** for Android.

Built with [Samanzamani's PersianDate](https://github.com/samanzamani/persianDate) for full Jalali calendar support. Designed for flexibility, accessibility, and ease of use across all Android API levels 21+.

This library is **based on and modified from** the open-source project [persian-material-datepicker](https://github.com/M-Erfan-Dm/persian-material-datepicker).  
Significant improvements were made to **reduce library size**, **optimize performance**, and **enhance modularity** to better fit production environments and modern Android app requirements.

---

## ✨ Features

- 📆 **Date Picker** – Select a single Jalali date
- 🗓 **Range Picker** – Pick a range between two Jalali dates
- ⏰ **Linear Time Picker** – Choose a time (HH:mm) with localization
- 🧠 **Persian holidays support**
- 🎨 Fully customizable via decorators & theming
- 🧩 Based on **Material Design** guidelines
- 🌍 Localization: Supports **Persian language** out of the box
- 📦 Easy to integrate via **JitPack**
- ✅ Minimum SDK: 21, Target SDK: 34

---

## 📦 Installation

> Available via [JitPack](https://jitpack.io)

1. Add JitPack to your **root** `build.gradle` or `settings.gradle`:

allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

2. Add the dependency to your module build.gradle

dependencies {
    implementation 'com.github.masoudalishahi:PrsianDateTimePicker:1.2.1'
}

## Usage
📆 Single Date Picker:

val datePicker = MaterialDatePicker.Builder.datePicker()
    .setTitleText("Select Date")
    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())  // Optional
    .setEnableHoliday(true)  // Optional
    .setDayViewDecorator(HolidayDecorator()) // Optional
    .setPositiveButtonText(R.string.confirm)
    .setNegativeButtonText(R.string.cancel)
    .build()

datePicker.show(supportFragmentManager, datePicker.toString())

datePicker.addOnPositiveButtonClickListener { selectedDate ->
    // selectedDate: Long (milliseconds)
}

📆 Date Range Picker:

val datePicker = MaterialDatePicker.Builder.dateRangePicker()
    .setTitleText("Select Date Range")
    .setEnableHoliday(true)
    .setPositiveButtonText(R.string.confirm)
    .build()

datePicker.show(supportFragmentManager, datePicker.toString())

datePicker.addOnPositiveButtonClickListener { range ->
    //val startDate = range.first  // Long
    //val endDate = range.second   // Long
}

⏰ Time Picker:

val timePicker = TimePickerBuilder(this)
    //.setInitialTime(10, 30)
    //.setTitle("Select Time")
    .setOnConfirmListener { selectedTime ->
        Toast.makeText(this, "Selected Time: $selectedTime", Toast.LENGTH_SHORT).show()
    }
timePicker.show()





