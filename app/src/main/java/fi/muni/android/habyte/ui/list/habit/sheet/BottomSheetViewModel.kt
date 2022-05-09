package fi.muni.android.habyte.ui.list.habit.sheet

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fi.muni.android.habyte.database.dao.HabitDao
import kotlinx.coroutines.launch

class BottomSheetViewModel(private val habitId: Int, private val habitDao: HabitDao) : ViewModel() {

    var path: Uri? = null

    fun updateHabyte(description: String?, photoUri: Uri?) {
        if (description == null && photoUri == null) {
            return
        }
        viewModelScope.launch {
            var oldHabit = habitDao.findHabitById(habitId)
            description?.let {
                oldHabit = oldHabit.copy(additionalText = description)
            }
            photoUri?.let {
                oldHabit = oldHabit.copy(photoPath = photoUri)
            }
            habitDao.updateHabit(oldHabit)
        }
    }

    fun savePhoto() {
        path?.let {
            updateHabyte(null, it)
        }
    }
}

class BottomSheetViewModelFactory(
    private val habyteId: Int,
    private val habitDao: HabitDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomSheetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BottomSheetViewModel(habyteId, habitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}