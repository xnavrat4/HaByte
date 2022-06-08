package fi.muni.android.habyte

import androidx.lifecycle.*
import ca.antonious.materialdaypicker.MaterialDayPicker
import fi.muni.android.habyte.database.dao.HabitDao
import fi.muni.android.habyte.database.dao.HabyteDao
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.model.Habyte
import fi.muni.android.habyte.util.daysToWeekday
import fi.muni.android.habyte.util.toMDPW
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
        startTime: LocalTime,
        endDate: LocalDate,
        selectedDays: Set<MaterialDayPicker.Weekday>
    ) {
        viewModelScope.launch {
            try {
                val habyte = Habyte(
                    id = 0,
                    name = title,
                    description = description,
                    startDate = LocalDate.now(),
                    endDate = endDate,
                    habitsFinished = 0,
                    habitsToDo = 0,
                    selectedDays = selectedDays,
                    selectedTime = startTime
                )

                val newHabyteId = habyteDao.insertHabyte(habyte).toInt()

                //Create Habits
                val habitsTodo: Int =
                    createHabits(newHabyteId, habyte, startTime, endDate, selectedDays)

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
        startTime: LocalTime,
        endDate: LocalDate,
        selectedDays: Set<MaterialDayPicker.Weekday>
    ) {
        viewModelScope.launch {
            if (!supportsUpdate()) {
                _queryResult.value =
                    Result.failure(IllegalArgumentException("Habyte ID unspecified"))
                return@launch
            }
            try {
                val oldHabyte = habyteDao.suspendFindHabyte(habyteId)

                val allHabits = habitDao.findHabitsByHabyte(habyteId)

                var lastDoneHabitIdx = 0

                var habitsTodo: Int = oldHabyte.habitsToDo
                if (selectedDays != oldHabyte.selectedDays || startTime != oldHabyte.selectedTime) {

                    for (habit in allHabits) {
                        // delete undone habits older than today
                        if (habit.start.toLocalDate() >= LocalDate.now() && !habit.done) {
                            habitDao.deleteHabit(habit)
                        } else {
                            lastDoneHabitIdx++
                        }
                    }

                    // recreate habits according to new specifications
                    habitsTodo = createHabits(
                        habyteId,
                        oldHabyte,
                        startTime,
                        endDate,
                        selectedDays,
                        lastDoneHabitIdx
                    )
                }

                // Update value for the habit
                val updatedHabyte = oldHabyte.copy(
                    name = title,
                    description = description,
                    endDate = endDate,
                    habitsToDo = habitsTodo,
                    selectedDays = selectedDays,
                    selectedTime = startTime
                )
                habyteDao.updateHabyte(updatedHabyte)
                _queryResult.value = Result.success(Unit)
            } catch (e: Throwable) {
                _queryResult.value = Result.failure(e)
            }

        }
    }

    private suspend fun createHabits(
        id: Int,
        habyte: Habyte,
        startTime: LocalTime,
        endDate: LocalDate,
        selectedDays: Set<MaterialDayPicker.Weekday>,
        counter: Int = 0
    ): Int {
        // SET TO NEXT DAY IF TODAY + START TIME IS IN THE PAST
        var time = if (startTime > LocalTime.now()) {
            LocalDateTime.of(LocalDate.now(), startTime)
        } else {
            LocalDateTime.of(LocalDate.now().plusDays(1), startTime)
        }

        time = adjustFirstDate(time, selectedDays)
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

            val nextDay = getNearestDay(time, selectedDays)
            x++
            time = time.plusDays(time.dayOfWeek.toMDPW().daysToWeekday(nextDay))
        }

        return x
    }

    private fun adjustFirstDate(
        time: LocalDateTime,
        selectedDays: Set<MaterialDayPicker.Weekday>
    ): LocalDateTime {
        val dow = time.dayOfWeek.toMDPW()
        if (!selectedDays.contains(dow)) {
            return time.plusDays(dow.daysToWeekday(getNearestDay(time, selectedDays)))
        }
        return time
    }

    private fun getNearestDay(
        time: LocalDateTime,
        selectedDays: Set<MaterialDayPicker.Weekday>
    ): MaterialDayPicker.Weekday {
        var nearestDay = selectedDays.stream().findFirst().get()
        val dow = time.dayOfWeek.toMDPW()

        for (selectedDay in selectedDays) {
            if (dow.daysToWeekday(selectedDay) < dow.daysToWeekday(nearestDay)) {
                nearestDay = selectedDay
            }
        }
        return nearestDay
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