package fi.muni.android.habyte.util

import fi.muni.android.habyte.model.Habit
import java.time.LocalDate

fun LocalDate.daysUntil(endDate: LocalDate) : Int {
    return this.until(endDate).days
}

fun Int.progressAsString(total: Int) : String {
    return "$this/$total"
}

fun List<Habit>.toIdsString() :String {
    return this.map { it -> it.id }
        .toString()
        .replace(" ", "")
        .removeSurrounding("[", "]")
}

