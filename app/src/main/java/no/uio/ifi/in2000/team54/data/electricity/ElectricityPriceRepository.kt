package no.uio.ifi.in2000.team54.data.electricity

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

class ElectricityPriceRepository(private val datasource: ElectricityPriceDatasource) {
    // https://forbrukerguiden.no/normalt-stromforbruk/
    private fun estimatedAvgKwh(): Double = 44.43 //Avg kWh for 120kvm enebolig per dag


    //Get the average electricity price with and without solar panel
    // The avg is in NOK per kWh
    suspend fun getPriceData(days: Int, area: String, solarProduction: Double): List<Double> {
        val avgPrice = getPriceDataInterval(days, area).average()
        return listOf((estimatedAvgKwh() - solarProduction) * days * avgPrice, avgPrice * days * estimatedAvgKwh())
    }

    //Get which month the calculations base themselves on
    //Decrement in order to get the most accurate month (e.g. for edge case 1st of the month)
    @SuppressLint("SimpleDateFormat")
    fun getMonth(): Int {
        val pattern = "yyyy/MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val currentDate = simpleDateFormat.format(Date())

        for (i in 0..14) {
            decrementDate(currentDate)
        }

        val info = currentDate.split("-", "/")
        val month = info[1]
        val monthToIndex = mapOf(
            "01" to 0,
            "02" to 1,
            "03" to 2,
            "04" to 3,
            "05" to 4,
            "06" to 5,
            "07" to 6,
            "08" to 7,
            "09" to 8,
            "10" to 9,
            "11" to 10,
            "12" to 11,
        )
        return monthToIndex[month]!!
    }


    @SuppressLint("SimpleDateFormat")
    suspend fun getPriceDataInterval(days: Int, area: String): List<Double> {
        val nokPerKwh: MutableList<Double> = mutableListOf()
        val pattern = "yyyy/MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        var currentDate = simpleDateFormat.format(Date())

        for (i in 0..<days) {
            if (days in 2..30 && i % 3 != 0) continue //Limit requests
            if (days > 30 && i % 14 != 0) continue //Limit requests
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