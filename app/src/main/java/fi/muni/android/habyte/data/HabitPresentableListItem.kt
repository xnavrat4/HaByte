package fi.muni.android.habyte.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class HabitPresentableListItem(
    val id: Long,
    val name: String,
    val time: LocalDateTime
) : Parcelable
