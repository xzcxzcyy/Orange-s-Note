package cloud.banson.orangeNote.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "details")
    var details: String = "",

    @ColumnInfo(name = "time")
    var time: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "importance")
    var importance: Long = 0,

    @ColumnInfo(name = "alarm_time")
    var alarmTime: Long = -1
)