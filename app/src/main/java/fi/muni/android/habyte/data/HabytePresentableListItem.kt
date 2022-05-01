package fi.muni.android.habyte.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HabytePresentableListItem(
    val id: Long,
    val name: String
) : Parcelable
