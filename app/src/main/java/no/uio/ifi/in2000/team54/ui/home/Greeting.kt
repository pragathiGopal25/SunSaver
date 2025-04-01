package no.uio.ifi.in2000.team54.ui.home

import java.util.Calendar

fun getGreeting(): String {

    val currentTime =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    return when (currentTime) {

        in 5 .. 9 -> "God morgen!"
        in 10 .. 13 -> "God formiddag!"
        in 14 .. 16 -> "God ettermiddag!"
        else -> "God kveld!"
    }
}