package cloud.banson.orangeNote.details

import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.databinding.BindingAdapter
import cloud.banson.orangeNote.database.Note

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
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }
}
