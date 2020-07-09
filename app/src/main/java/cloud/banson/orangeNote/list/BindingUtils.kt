package cloud.banson.orangeNote.list

import android.widget.TextView
import androidx.databinding.BindingAdapter
import cloud.banson.orangeNote.database.Note

@BindingAdapter("titleText")
fun TextView.setTitleText(item: Note?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("idText")
fun TextView.setIdText(item: Note?) {
    item?.let {
        text = item.id.toString()
    }
}
