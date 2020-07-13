package cloud.banson.orangeNote

import java.text.DateFormat.getDateTimeInstance

fun Long.toDateString(): String {
    return getDateTimeInstance()
        .format(this).toString()
}