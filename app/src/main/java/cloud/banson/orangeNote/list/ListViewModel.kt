package cloud.banson.orangeNote.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.database.NoteDatabaseDao
import kotlinx.coroutines.*

class ListViewModel(private val database: NoteDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    var noteBook = database.getAllNotes()

    private val _navigateToDetailsFragment = MutableLiveData<Boolean>()

    val navigateToDetailsFragment: LiveData<Boolean>
        get() = _navigateToDetailsFragment

    fun doneNavigating() {
        _navigateToDetailsFragment.value = false
    }

    fun onNoteAddClicked() {
        /*val newNote = Note()
        newNote.title = "sample_title"
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.insert(newNote)
            }
        }*/
        _navigateToDetailsFragment.value = true
    }

}