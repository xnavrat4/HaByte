package fi.muni.android.habyte.repository

import android.content.Context
import fi.muni.android.habyte.data.HabytePresentableListItem

class HabyteRepository(
    context: Context
) {

    fun getMockedData(count: Int = 10) : List<HabytePresentableListItem> =
        mutableListOf<HabytePresentableListItem>().apply {
            repeat(count) {
                val habyte = HabytePresentableListItem(
                    id = it.toLong(),
                    name = "Habyte-$it"
                )
                add(habyte)
            }
        }

}