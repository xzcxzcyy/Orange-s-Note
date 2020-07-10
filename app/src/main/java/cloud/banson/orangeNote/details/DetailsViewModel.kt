package cloud.banson.orangeNote.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.database.NoteDatabaseDao
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
    var title = MutableLiveData<String>()
    var details = MutableLiveData<String>()
    val currentNote = MediatorLiveData<Note>()

    private val _navigateToListFragment = MutableLiveData<Boolean>()
    private val _makeSnackBar = MutableLiveData<String>()

    init {
        currentNote.addSource(database.getNoteById(currentNoteId), currentNote::setValue)
    }

    fun onCompleteButtonClicked() {
//        val newNote = Note(title = this.title.value, )
//        Log.d("TestDataCapturing", "onCompleteButtonClicked: " + title.value)
//        Log.d("TestDataCapturing", "onCompleteButtonClicked: " + details.value)

        if (title.value == null) {
            _makeSnackBar.value = "事件名不能为空！"
        } else {
            if (details.value == null) {
                details.value = ""
            }

            val newTitle = this.title.value
            val newDetails = this.details.value
            val newNote = Note(title = newTitle!!, details = newDetails!!)

            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database.insert(newNote)
                }
            }

            _navigateToListFragment.value = true
        }
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

}