package fi.muni.android.habyte.util

import java.time.LocalDate

fun LocalDate.daysUntil(endDate: LocalDate) : Int {
    return this.until(endDate).days
}

fun Int.progressAsString(total: Int) : String {
    return "$this/$total"
}

