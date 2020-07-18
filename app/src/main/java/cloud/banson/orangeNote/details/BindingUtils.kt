package cloud.banson.orangeNote.details

import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.databinding.BindingAdapter
import cloud.banson.orangeNote.R
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.toDateString
import cloud.banson.orangeNote.toTimeString
import java.util.*

@BindingAdapter("switchState")
fun Switch.setCheckStatus(note: Note?) {
    note?.let {
        this.isChecked = (note.alarmTime >= 0)
    }
}

@BindingAdapter("buttonVisibleControl")
fun Button.setVisibility(note: Note?) {
    note?.let {
        if (note.alarmTime >= 0) {
            if (this.id == R.id.buttonAlarmDate) {
                this.text = note.alarmTime.toDateString()
            } else if (this.id == R.id.buttonAlarmTime) {
                this.text = note.alarmTime.toTimeString()
            }
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }
}
