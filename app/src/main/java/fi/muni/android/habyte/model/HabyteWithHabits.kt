package fi.muni.android.habyte.model

import androidx.room.Embedded
import androidx.room.Relation


data class HabyteWithHabits (

    @Embedded
    val habyte: Habyte,

    @Relation(
        parentColumn = "id",
        entityColumn = "habyteId"
    )
    val habits: List<Habit>
)