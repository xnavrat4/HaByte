package fi.muni.android.habyte.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.antonious.materialdaypicker.MaterialDayPicker
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Entity(tableName = "habyte")
@Parcelize
data class Habyte(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "startDate")
    val startDate: LocalDate,

    @ColumnInfo(name = "endDate")
    val endDate: LocalDate,

    @ColumnInfo(name = "habitsFinished")
    val habitsFinished: Int,

    @ColumnInfo(name = "habitsToDo")
    val habitsToDo: Int,

    @ColumnInfo(name = "currentlyPickedDays")
    val selectedDays: Set<MaterialDayPicker.Weekday>,

    @ColumnInfo(name = "selectedTime")
    val selectedTime: LocalTime
) : Parcelable