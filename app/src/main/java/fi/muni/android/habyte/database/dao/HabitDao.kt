package fi.muni.android.habyte.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.model.HabyteWithHabits
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Dao
interface HabitDao {

    @Query("SELECT * FROM habit WHERE id = :habitId")
    fun findHabitById(habitId: Int): Habit

    @Transaction
    @Query("SELECT * FROM habit WHERE habyteId = :habyteId order by start")
    suspend fun findHabitsByHabyte(habyteId: Int): List<Habit>

    @Transaction
    @Query("SELECT * FROM habit WHERE habyteId = :habyteId AND start >= :date")
    fun findHabitsWithHabyteOlderThan(habyteId: Int, date: LocalDateTime): Flow<List<Habit>>

    @Transaction
    @Query("SELECT * FROM habit WHERE habyteId = :habyteId AND start = :date")
    fun findHabitsWithHabyteOnDate(habyteId: Int, date: LocalDateTime): Flow<List<Habit>>

    @Transaction
    @Query(
        "SELECT *" +
                "FROM habit" +
                " WHERE start >= :date and start <= :date + 86400 and done = :finished" +
                " order by start"
    )
    fun findHabitsOnDate(date: LocalDate, finished: Boolean): Flow<List<Habit>>

    @Transaction
    @Query("SELECT * FROM habit")
    fun findHabits(): Flow<List<Habit>>

    @Insert
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)
}