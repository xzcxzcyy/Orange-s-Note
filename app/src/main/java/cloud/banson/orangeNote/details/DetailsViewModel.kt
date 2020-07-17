package cloud.banson.orangeNote.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.database.NoteDatabaseDao
import cloud.banson.orangeNote.toDateTimeString
import kotlinx.coroutines.*

class DetailsViewModel(
    private val database: NoteDatabaseDao,
    application: Application,
    currentNoteId: Long
) : AndroidViewModel(application) {
    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    var noteBook = database.getAllNotes()

//    var title = MutableLiveData<String>()
//    var details = MutableLiveData<String>()

    val currentNote = MediatorLiveData<Note>()

    private val _navigateToListFragment = MutableLiveData<Boolean>()
    private val _makeSnackBar = MutableLiveData<String>()

    init {
        currentNote.addSource(database.getNoteById(currentNoteId), currentNote::setValue)
    }

    suspend fun update(newNote: Note) {
        withContext(Dispatchers.IO) {
            database.update(newNote)
        }
    }

    fun onCompleteButtonClicked() {

        if (currentNote.value?.title == "") {
            _makeSnackBar.value = "未输入事件名：将使用默认事件名。"
            currentNote.value?.title = "新记事@ " + currentNote.value!!.time.toDateTimeString()
        }
        if (currentNote.value?.details == null) {
            currentNote.value?.details = ""
        }

        uiScope.launch {
            update(currentNote.value!!)
        }

        _navigateToListFragment.value = true

    }

    val navigateToListFragment: LiveData<Boolean>
        get() = _navigateToListFragment

    val makeSnackBar: LiveData<String>
        get() = _makeSnackBar

    fun doneNavigating() {
        _navigateToListFragment.value = false
    }

    fun doneMakeSnackBar() {
        _makeSnackBar.value = null
    }

    fun switchStatusChanged(status: Boolean) {
        uiScope.launch {
            if (status) {
                currentNote.value!!.alarmTime = System.currentTimeMillis()
            } else {
                currentNote.value!!.alarmTime = -1
            }
            update(currentNote.value!!)
        }
    }
}