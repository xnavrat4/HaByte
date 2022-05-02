package fi.muni.android.habyte.repository

import android.content.Context
import fi.muni.android.habyte.data.HabytePresentableListItem
import java.time.LocalDate
import java.util.*

class HabyteRepository(context: Context) {

    fun getMockedData(count: Int = 10) : List<HabytePresentableListItem> =
        mutableListOf<HabytePresentableListItem>().apply {
            repeat(count) {
                val habyte = HabytePresentableListItem(
                    id = it.toLong(),
                    name = "Habyte-$it",
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().plusDays(1 + it.toLong()),
                    progress = 1
                )
                add(habyte)
            }
        }

    fun getById(id: Long) : HabytePresentableListItem {
        return HabytePresentableListItem(
            id = id,
            name = "random, no database yet" ,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(1 + id),
            progress = 1
        )
    }
}