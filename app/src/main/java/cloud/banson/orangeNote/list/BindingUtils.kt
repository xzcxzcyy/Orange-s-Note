package cloud.banson.orangeNote.list

import android.widget.TextView
import androidx.databinding.BindingAdapter
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.toDateTimeString

@BindingAdapter("titleText")
fun TextView.setTitleText(item: Note?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("timeText")
fun TextView.setTimeText(item: Note?) {
    item?.let {
        text = item.time.toDateTimeString()
    }
}
