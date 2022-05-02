package fi.muni.android.habyte.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.*

@Parcelize
data class HabytePresentableListItem(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val progress: Int
) : Parcelable
