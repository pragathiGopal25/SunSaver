package no.uio.ifi.in2000.team54.data.electricity

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

class ElectricityPriceRepository(private val datasource: ElectricityPriceDatasource) {
    fun fakeAvgKwh(): Double = 50.6 //Enebolig kWh produsert daglig avg


    //Get the average electricity price
    // The avg is in NOK per kWh
    suspend fun getPriceData(days: Int, area: String): Double {
        return getPriceDataInterval(days, area).average() * days //* avg kWh usage per day
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun getPriceDataInterval(days: Int, area: String): List<Double> {
        val nokPerKwh: MutableList<Double> = mutableListOf()
        val pattern = "yyyy/MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        var currentDate = simpleDateFormat.format(Date())

        for (i in 0..<days) {
            if (days in 2..30 && i % 2 != 0) continue //Limit requests
            if (days > 30 && i % 7 != 0) continue //Limit requests
            datasource.getElectricityPrices(area, currentDate).forEach {
                nokPerKwh.add(it.nokPrKiloWh)
            }
            currentDate = decrementDate(currentDate)
        }
        return nokPerKwh
    }

    private fun decrementDate(date: String): String {
        val months = mapOf(
            "01" to "31", "02" to "28", "03" to "31", "04" to "30",
            "05" to "31", "06" to "30", "07" to "31", "08" to "31", "09" to "30",
            "10" to "31", "11" to "30", "12" to "31"
        )

        fun decrement(value: String): String = (value.toInt() - 1).toString()
        val info = date.split("-", "/")
        var year = info[0]
        var month = info[1]
        var day = info[2]

        day = decrement(day)
        if (day.length < 2) day = "0$day"
        if (month == "03" && year.toInt() % 4 == 0 && (year.toInt() % 100 != 0 || year.toInt() % 400 == 0)) {
            day = "29"
            month = "02"
        } else if (day < "01") {
            month = decrement(month)
            if (month.length < 2) month = "0$month"
            day = months[month].toString()
        }
        if (month < "01" && day == "null") { //month becomes "00" which isn't in the map so day becomes null.toString()
            year = decrement(year)
            month = "12"
            day = months[month].toString()
        }

        val newDate = "$year/$month-$day"
        return newDate
    }
}