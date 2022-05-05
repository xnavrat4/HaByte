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

class AddOrUpdateHabyteViewModel(
    private val habyteDao: HabyteDao,
    private val habitDao: HabitDao,
    private val habyteId: Int
) :
    ViewModel() {

    private var _queryResult: MutableLiveData<Result<Unit>> = MutableLiveData()

    private val _habyte: LiveData<Habyte> by lazy {
        habyteDao.findHabyte(habyteId).asLiveData()
    }

    val habyte: LiveData<Habyte>
        get() = _habyte

    val queryResult: LiveData<Result<Unit>>
        get() = _queryResult

    fun createHabyte(
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

                val newHabyteId = habyteDao.insertHabyte(habyte).toInt()

                //Create Habits
                val habitsTodo: Int = when (recurrenceString) {
                    "Daily" -> createHabitsDaily(newHabyteId, habyte, startLocalTime, endDateLD)
                    "Weekly" -> createHabitsWeekly(newHabyteId, habyte, endDateLD)
                    else -> 0
                }

                // Update value for the habyte
                val updatedHabyte = habyte.copy(id = newHabyteId, habitsToDo = habitsTodo)
                habyteDao.updateHabyte(updatedHabyte)
                _queryResult.value = Result.success(Unit)
            } catch (e: Throwable) {
                _queryResult.value = Result.failure(e)
            }

        }
    }

    fun supportsUpdate(): Boolean {
        return habyteId != -1
    }

    fun updateHabyte(
        title: String,
        description: String,
        startTime: String,
        recurrenceString: String,
        endDate: String
    ) {
        viewModelScope.launch {
            if (!supportsUpdate()) {
                _queryResult.value =
                    Result.failure(IllegalArgumentException("Habyte ID unspecified"))
                return@launch
            }
            try {
                val endDateLD: LocalDate = LocalDate.parse(
                    endDate,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
                val startLocalTime = LocalTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_TIME)

                val oldHabyte = habyteDao.suspendFindHabyte(habyteId)

                val allHabits = habitDao.findHabitsByHabyte(habyteId)

                var lastDoneHabitIdx = 0
                for (habit in allHabits) {
                    // delete undone habits older than today
                    if (habit.start.toLocalDate() >= LocalDate.now() && !habit.done) {
                        habitDao.deleteHabit(habit)
                    } else {
                        lastDoneHabitIdx++
                    }
                }

                // recreate habits according to new specifications
                val habitsTodo: Int = when (recurrenceString) {
                    "Daily" -> createHabitsDaily(
                        habyteId,
                        oldHabyte,
                        startLocalTime,
                        endDateLD,
                        lastDoneHabitIdx
                    )
                    "Weekly" -> createHabitsWeekly(habyteId, oldHabyte, endDateLD)
                    else -> 0
                }

                // Update value for the habit
                val updatedHabyte = oldHabyte.copy(
                    name = title,
                    description = description,
                    endDate = endDateLD,
                    habitsToDo = habitsTodo
                )
                habyteDao.updateHabyte(updatedHabyte)
                _queryResult.value = Result.success(Unit)
            } catch (e: Throwable) {
                _queryResult.value = Result.failure(e)
            }

        }
    }

    private fun createHabitsWeekly(id: Int, habyte: Habyte, endDate: LocalDate): Int {
        TODO("Not yet implemented")
    }

    private suspend fun createHabitsDaily(
        id: Int,
        habyte: Habyte,
        startTime: LocalTime,
        endDate: LocalDate,
        counter: Int = 0
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

        var x = counter
        while (time <= endDateTime) {
            val habit = Habit(
                0,
                "#${x + 1} ${habyte.name}",
                time,
                null,
                null,
                false,
                id
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

class AddOrUpdateHabyteViewModelFactory(
    private val habyteDao: HabyteDao,
    private val habitDao: HabitDao,
    private val habyteId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddOrUpdateHabyteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddOrUpdateHabyteViewModel(habyteDao, habitDao, habyteId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}