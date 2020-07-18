package cloud.banson.orangeNote

import android.annotation.SuppressLint
import java.text.DateFormat.*
import java.text.SimpleDateFormat

fun Long.toDateTimeString(): String {
    return getDateTimeInstance()
        .format(this).toString()
}

fun Long.toDateString(): String {
    return getDateInstance()
        .format(this).toString()
}

@SuppressLint("SimpleDateFormat")
fun Long.toTimeString(): String {
    return SimpleDateFormat("HH:mm")
        .format(this).toString()
}

@SuppressLint("SimpleDateFormat")
fun Long.getYear(): String {
    return SimpleDateFormat("yyyy")
        .format(this).toString()
}
