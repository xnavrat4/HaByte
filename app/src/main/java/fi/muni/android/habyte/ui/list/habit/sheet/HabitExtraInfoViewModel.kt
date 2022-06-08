package fi.muni.android.habyte.ui.list.habit.sheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import fi.muni.android.habyte.database.dao.HabitDao
import fi.muni.android.habyte.model.Habit

class HabitExtraInfoViewModel(private val habitId: Int, private val habitDao: HabitDao) : ViewModel() {
    private val _habit: LiveData<Habit> by lazy {
        habitDao.findHabitByIdFlow(habitId).asLiveData()
    }

    val habit: LiveData<Habit>
        get() = _habit
}

class HabitExtraInfoViewModelFactory(
    private val habyteId: Int,
    private val habitDao: HabitDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitExtraInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitExtraInfoViewModel(habyteId, habitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}