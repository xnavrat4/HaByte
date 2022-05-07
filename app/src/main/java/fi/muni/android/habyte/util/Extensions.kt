package fi.muni.android.habyte.util

import ca.antonious.materialdaypicker.MaterialDayPicker
import java.time.DayOfWeek
import java.time.LocalDate

fun LocalDate.daysUntil(endDate: LocalDate) : Int {
    return this.until(endDate).days
}

fun Int.progressAsString(total: Int) : String {
    return "$this/$total"
}

fun MaterialDayPicker.Weekday.daysToWeekday(materialDayPicker: MaterialDayPicker.Weekday) : Long {
    if (this.ordinal >= materialDayPicker.ordinal) {
        return 7L - this.ordinal + materialDayPicker.ordinal
    }
    return (materialDayPicker.ordinal - this.ordinal).toLong()
}

fun DayOfWeek.toMDPW(): MaterialDayPicker.Weekday {
    return when (this) {
        DayOfWeek.MONDAY -> MaterialDayPicker.Weekday.MONDAY
        DayOfWeek.TUESDAY -> MaterialDayPicker.Weekday.TUESDAY
        DayOfWeek.WEDNESDAY -> MaterialDayPicker.Weekday.WEDNESDAY
        DayOfWeek.THURSDAY -> MaterialDayPicker.Weekday.THURSDAY
        DayOfWeek.FRIDAY -> MaterialDayPicker.Weekday.FRIDAY
        DayOfWeek.SATURDAY -> MaterialDayPicker.Weekday.SATURDAY
        DayOfWeek.SUNDAY -> MaterialDayPicker.Weekday.SUNDAY
    }
}

fun MaterialDayPicker.Weekday.toDOW(): DayOfWeek {
    return when (this) {
        MaterialDayPicker.Weekday.SUNDAY -> DayOfWeek.SUNDAY
        MaterialDayPicker.Weekday.MONDAY -> DayOfWeek.MONDAY
        MaterialDayPicker.Weekday.TUESDAY -> DayOfWeek.TUESDAY
        MaterialDayPicker.Weekday.WEDNESDAY -> DayOfWeek.WEDNESDAY
        MaterialDayPicker.Weekday.THURSDAY -> DayOfWeek.THURSDAY
        MaterialDayPicker.Weekday.FRIDAY -> DayOfWeek.FRIDAY
        MaterialDayPicker.Weekday.SATURDAY -> DayOfWeek.SATURDAY
    }
}