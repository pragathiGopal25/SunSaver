package no.uio.ifi.in2000.team54.util

fun String.isNumber(): Boolean = when {
    isEmpty() -> false
    count { it == '.' } > 1 -> false
    else -> all { it.isDigit() || it == '.' }
}
