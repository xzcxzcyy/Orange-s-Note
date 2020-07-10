package cloud.banson.orangeNote.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDatabaseDao {
    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Query("SELECT * FROM note_table WHERE id=:targetId")
    fun get(targetId: Long): Note?

    @Query("SELECT * FROM note_table WHERE id=:targetId")
    fun getNoteById(targetId: Long): LiveData<Note>

    @Query("SELECT * FROM note_table")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("DELETE FROM note_table")
    fun clear()

    @Query("SELECT * FROM note_table ORDER BY id DESC LIMIT 1")
    fun getCurrentNote(): Note?
}