package no.uio.ifi.in2000.team54.util

fun String.isNumber(): Boolean {
    val regex = """^\d*\.?\d*$""".toRegex()
    return this.matches(regex)
}
