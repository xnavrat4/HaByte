package fi.muni.android.habyte.ui.list.habyte

import androidx.lifecycle.*
import fi.muni.android.habyte.database.dao.HabyteDao
import fi.muni.android.habyte.model.Habyte

class HabyteListViewModel(private val dao: HabyteDao) : ViewModel() {

    private val allHabytes: LiveData<List<Habyte>> by lazy {
        dao.findAllHabytes().asLiveData()
    }

    fun getHabytes(): LiveData<List<Habyte>> {
        return allHabytes
    }

}

class HabyteListViewModelFactory(private val dao: HabyteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabyteListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabyteListViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}