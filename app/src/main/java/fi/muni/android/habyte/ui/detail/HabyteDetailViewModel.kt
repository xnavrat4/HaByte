package fi.muni.android.habyte.ui.detail

import androidx.lifecycle.*
import fi.muni.android.habyte.database.dao.HabitDao
import fi.muni.android.habyte.database.dao.HabyteDao
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.model.Habyte
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class HabyteDetailViewModel (
    private val habyteId: Int,
    private val habyteDao: HabyteDao,
    private val habitDao: HabitDao
) : ViewModel() {

    private var _currentSelectedDate: MutableLiveData<LocalDate> = MutableLiveData()

    val currentSelectedDate: LiveData<LocalDate>
        get() = _currentSelectedDate

    public fun setCurrentSelectedDate(date : LocalDate){
        _currentSelectedDate.value = date
    }

    private val habyte: LiveData<Habyte> by lazy {
        habyteDao.findHabyte(habyteId).asLiveData()
    }

    private val _habitsOfHabyte: LiveData<List<Habit>> by lazy {
        habitDao.findHabitsByHabyteId(habyteId).asLiveData()
    }

    suspend fun getHabitesOfHabyteId(): List<Habit> {
        return habitDao.findHabitsByHabyte(habyteId)
    }
    fun getHabitesOfHabyte(): LiveData<List<Habit>> {
        return _habitsOfHabyte;
    }

    fun observeHabyte(): LiveData<Habyte> {
        return habyte
    }

    fun deleteHabyte() {
        viewModelScope.launch {
            val toDelete = habyteDao.suspendFindHabyte(habyteId)
            habyteDao.deleteHabyte(toDelete)
        }
    }

}

class HabyteDetailViewModelFactory(
    private val habyteId: Int,
    private val habyteDao: HabyteDao,
    private val habitDao: HabitDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabyteDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabyteDetailViewModel(habyteId, habyteDao, habitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}