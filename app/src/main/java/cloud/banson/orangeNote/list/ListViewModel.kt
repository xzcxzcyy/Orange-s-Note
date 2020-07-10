package cloud.banson.orangeNote.list

import android.app.Application
import android.util.Log
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

    var currentNote = MutableLiveData<Note>()

    private val _navigateToDetailsFragment = MutableLiveData<Long>()

    val navigateToDetailsFragment: LiveData<Long>
        get() = _navigateToDetailsFragment

    fun doneNavigating() {
        _navigateToDetailsFragment.value = null
    }

    private suspend fun insert(newNote: Note) {
        withContext(Dispatchers.IO) {
            database.insert(newNote)
        }
    }

    private suspend fun getCurrentNote(): Note? {
        return withContext(Dispatchers.IO) {
            database.getCurrentNote()
        }
    }

    fun onNoteAddClicked() {
        uiScope.launch {
            val passingNote = Note()
            passingNote.apply {
                title = ""
                details = ""
            }
            insert(passingNote)
            currentNote.value = getCurrentNote()
            _navigateToDetailsFragment.value = currentNote.value!!.id
        }
    }

    fun onItemClicked(noteId: Long) {
        _navigateToDetailsFragment.value = noteId
    }

}