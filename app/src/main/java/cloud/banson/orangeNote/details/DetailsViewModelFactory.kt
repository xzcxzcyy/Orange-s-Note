package cloud.banson.orangeNote.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.database.NoteDatabaseDao

class DetailsViewModelFactory(
    private val dataSource: NoteDatabaseDao,
    private val application: Application,
    private val currentNote: Note
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(dataSource, application, currentNote) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}