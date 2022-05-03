package fi.muni.android.habyte.database.dao

import androidx.room.*
import fi.muni.android.habyte.model.Habyte
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.*

@Dao
interface HabyteDao {

    @Query("SELECT * FROM habyte")
    fun findAllHabytes(): Flow<List<Habyte>>

    @Query("SELECT * from habyte WHERE id = :id")
    fun findHabyte(id: Int) : Flow<Habyte>

    @Query("SELECT * from habyte WHERE id = :id")
    suspend fun suspendFindHabyte(id: Int) : Habyte

    @Query("SELECT * FROM habyte WHERE startDate = :date")
    fun findHabyteStartedOn(date: LocalDate) : List<Habyte>

    @Insert
    suspend fun insertHabyte(habyte: Habyte): Long

    @Update
    suspend fun updateHabyte(habyte: Habyte)

    @Delete
    suspend fun deleteHabyte(habyte: Habyte)
}