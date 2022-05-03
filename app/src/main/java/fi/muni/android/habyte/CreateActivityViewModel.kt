package fi.muni.android.habyte

import androidx.lifecycle.*
import fi.muni.android.habyte.database.dao.HabitDao
import fi.muni.android.habyte.database.dao.HabyteDao
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.model.Habyte
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CreateActivityViewModel(private val habyteDao: HabyteDao, private val habitDao: HabitDao) :
    ViewModel() {

    private var _queryResult:MutableLiveData<Result<Unit>> = MutableLiveData()

    val queryResult: LiveData<Result<Unit>>
        get() = _queryResult

    fun populateHabyte(
        title: String,
        description: String,
        startTime: String,
        recurrenceString: String,
        endDate: String
    ) {
        viewModelScope.launch {
            try {
                val endDateLD: LocalDate = LocalDate.parse(
                    endDate,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
                val startLocalTime = LocalTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_TIME)
                val habyte = Habyte(
                    id = 0,
                    name = title,
                    description = description,
                    startDate = LocalDate.now(),
                    endDate = endDateLD,
                    habitsFinished = 0,
                    habitsToDo = 0
                )

                val newHabyteId = habyteDao.insertHabyte(habyte)

                //Create Habits
                val habitsTodo: Int = when (recurrenceString) {
                    "Daily" -> createHabitsDaily(newHabyteId, habyte, startLocalTime, endDateLD)
                    "Weekly" -> createHabitsWeekly(newHabyteId, habyte, endDateLD)
                    else -> 0
                }

                // Update value for the habit
                val updatedHabyte = habyte.copy(id = newHabyteId.toInt(), habitsToDo = habitsTodo)
                habyteDao.updateHabyte(updatedHabyte)
                _queryResult.value = Result.success(Unit)
            } catch (e: Throwable) {
                _queryResult.value = Result.failure(e)
            }

        }
    }

    private fun createHabitsWeekly(id: Long, habyte: Habyte, endDate: LocalDate): Int {
        TODO("Not yet implemented")
    }

    private suspend fun createHabitsDaily(
        id: Long,
        habyte: Habyte,
        startTime: LocalTime,
        endDate: LocalDate
    ): Int {

        var time = if (startTime > LocalTime.now()) {
            LocalDateTime.of(LocalDate.now(), startTime)
        } else {
            LocalDateTime.of(LocalDate.now().plusDays(1), startTime)
        }

        val endDateTime = endDate.atTime(23, 59, 59)
        if (time > endDateTime) {
            throw java.lang.IllegalArgumentException("THIS SHOULD BE VALIDATED")
        }

        var x = 0
        while (time <= endDateTime) {
            val habit = Habit(
                0,
                "#${x + 1}${habyte.name}",
                time,
                null,
                null,
                false,
                id.toInt()
            )
            habitDao.insertHabit(habit)
            x++
            time = time.plusDays(1)
        }

        return x
    }

    private fun createHabitsHourly(id: Long, habyte: Habyte, endDate: LocalDate): Int {
        TODO("Not yet implemented")
    }

}

class CreateActivityViewModelFactory(
    private val habyteDao: HabyteDao,
    private val habitDao: HabitDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateActivityViewModel(habyteDao, habitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}