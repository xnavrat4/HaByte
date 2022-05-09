package fi.muni.android.habyte.ui.list.habit

import androidx.lifecycle.*
import fi.muni.android.habyte.database.dao.HabitDao
import fi.muni.android.habyte.database.dao.HabyteDao
import fi.muni.android.habyte.model.Habit
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HabitListViewModel(private val dao: HabitDao, private val habyteDao: HabyteDao): ViewModel() {

    private val todaysHabits: LiveData<List<Habit>> by lazy {
        dao.findHabitsOnDate(LocalDate.now(), false).asLiveData()
    }

    fun getHabitsForToday() : LiveData<List<Habit>> {
        return todaysHabits
    }

    suspend fun getHabitsOfHabyte(id : Int) : List<Habit> {
        return dao.findHabitsByHabyte(id).toList();
    }

    fun confirmHabit(listItem: Habit) {
        viewModelScope.launch {
            dao.updateHabit(listItem.copy(done = true))
            val habyteToUpdate = habyteDao.suspendFindHabyte(listItem.habyteId)
            habyteDao.updateHabyte(habyteToUpdate.copy(habitsFinished = habyteToUpdate.habitsFinished+1))
        }
    }
}

class HabitListViewModelFactory(private val dao: HabitDao, private val habyteDao: HabyteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitListViewModel(dao, habyteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}