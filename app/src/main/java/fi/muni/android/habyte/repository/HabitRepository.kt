package fi.muni.android.habyte.repository

import android.content.Context
import fi.muni.android.habyte.data.HabitPresentableListItem
import java.time.LocalDateTime

class HabitRepository(context: Context) {
    fun getMockedData(count: Int = 20) : List<HabitPresentableListItem> =
        mutableListOf<HabitPresentableListItem>().apply {
            repeat(count) {
                val habit = HabitPresentableListItem(
                    id = it.toLong(),
                    name = "Habit-$it",
                    time = LocalDateTime.now().plusHours(it.toLong())
                )
                add(habit)
            }
        }
}