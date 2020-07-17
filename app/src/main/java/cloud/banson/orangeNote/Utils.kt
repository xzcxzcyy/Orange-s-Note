package cloud.banson.orangeNote

import java.text.DateFormat.*

fun Long.toDateTimeString(): String {
    return getDateTimeInstance()
        .format(this).toString()
}

fun Long.toDateString(): String {
    return getDateInstance()
        .format(this).toString()
}

fun Long.toTimeString(): String {
    return getTimeInstance()
        .format(this).toString()
}
