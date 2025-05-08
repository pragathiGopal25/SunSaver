package no.uio.ifi.in2000.team54.ui.home

import java.util.Calendar

fun getGreeting(): String {

    val currentTime =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    return when (currentTime) {

        in 5 .. 9 -> "God morgen!"
        in 10 .. 11 -> "God formiddag!"
        in 12 .. 17 -> "God ettermiddag!"
        else -> "God kveld!"
    }
}