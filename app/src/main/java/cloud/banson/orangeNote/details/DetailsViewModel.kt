package cloud.banson.orangeNote.details

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.database.NoteDatabaseDao
import cloud.banson.orangeNote.toDateString
import cloud.banson.orangeNote.toDateTimeString
import cloud.banson.orangeNote.toTimeString
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

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

    val currentNote = MediatorLiveData<Note>()
    val alarmDate = Transformations.map(currentNote) { currentNote ->
        currentNote.alarmTime.toDateString()
    }
    val alarmTime = Transformations.map(currentNote) { currentNote ->
        currentNote.alarmTime.toTimeString()
    }

    private val _navigateToListFragment = MutableLiveData<Boolean>()
    private val _makeSnackBar = MutableLiveData<String>()
    private val _makeTimePicker = MutableLiveData<Boolean>()
    private val _makeDatePicker = MutableLiveData<Boolean>()

    init {
        currentNote.addSource(database.getNoteById(currentNoteId), currentNote::setValue)
    }

    private suspend fun update(newNote: Note) {
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

    val makeTimePicker: LiveData<Boolean>
        get() = _makeTimePicker

    val makeDatePicker: LiveData<Boolean>
        get() = _makeDatePicker

    fun doneNavigating() {
        _navigateToListFragment.value = false
    }

    fun doneMakeSnackBar() {
        _makeSnackBar.value = null
    }

    fun switchStatusChanged(status: Boolean) {
        uiScope.launch {
            if (status) {
                if (currentNote.value!!.alarmTime == (-1).toLong()) {
                    currentNote.value!!.alarmTime = System.currentTimeMillis()
                }
            } else {
                currentNote.value!!.alarmTime = -1
            }
            update(currentNote.value!!)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun doneTimeClick(hourOfDay: Int, minute: Int) {
        uiScope.launch {
            val dateString =
                SimpleDateFormat("yyyy-MM-dd").format(currentNote.value!!.alarmTime).toString()
            val hourOfDayString = String.format("%02d", hourOfDay)
            val minuteString = String.format("%02d", minute)
            val formattedTime = "$dateString-$hourOfDayString-$minuteString"
            val unix = SimpleDateFormat("yyyy-MM-dd-HH-mm").parse(formattedTime)?.time
            currentNote.value!!.alarmTime = unix!!
            update(currentNote.value!!)
            //Log.d("DetailsViewModel", "doneTimeClick: ${currentNote.value!!.alarmTime}")
        }
        _makeTimePicker.value = false
    }

    @SuppressLint("SimpleDateFormat")
    fun doneDateClick(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        uiScope.launch {
            val timeString =
                SimpleDateFormat("HH-mm").format(currentNote.value!!.alarmTime).toString()
            val yearString = String.format("%04d", year)
            val monthOfYearString = String.format("%02d", monthOfYear)
            val dayOfMonthString = String.format("%02d", dayOfMonth)
            val formattedTime = "$yearString-$monthOfYearString-$dayOfMonthString-$timeString"
            Log.d("DetailsViewModel", "doneDateClick: $monthOfYear")
            val unix = SimpleDateFormat("yyyy-MM-dd-HH-mm").parse(formattedTime)?.time
            currentNote.value!!.alarmTime = unix!!
            update(currentNote.value!!)
        }
        _makeDatePicker.value = false
    }

    fun onTimeClick() {
        _makeTimePicker.value = true
    }

    fun onDateClick() {
        _makeDatePicker.value = true
    }
}
