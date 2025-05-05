package no.uio.ifi.in2000.team54.data.electricity

import android.annotation.SuppressLint
import no.uio.ifi.in2000.team54.domain.SolarArray
import java.text.SimpleDateFormat
import java.util.Date

class ElectricityPriceRepository(private val datasource: ElectricityPriceDatasource) {

    //Get the average electricity price with and without solar panel
    // The avg is in NOK per kWh
    fun getPriceData(
        days: Int,
        dailySolarPowerGeneration: Double,
        monthlyPowerConsumption: Double,
        avgDailyElectricityPrice: Double
    ): List<Double> {
        val dailyPowerConsumption = monthlyPowerConsumption / 30.0
        return listOf(
            (dailyPowerConsumption - dailySolarPowerGeneration) * days * avgDailyElectricityPrice,
            avgDailyElectricityPrice * days * dailyPowerConsumption
        )
    }

    //Get which month the calculations base themselves on
    //Decrement in order to get the most accurate month (e.g. for edge case 1st of the month)
    @SuppressLint("SimpleDateFormat")
    fun getMonth(): Int {
        val pattern = "yyyy/MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val currentDate = simpleDateFormat.format(Date())

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

    //Definerer de 5 sonene for strømpriser (strøm har forskjellig pris forskjellige deler av landet)
    //Dette er en rough estimat fordi de sonene har ganske kompliserte grenser
    fun getPriceArea(solarArray: SolarArray): String {
        val coords = solarArray.coordinates
        return when {
            coords.latitude > 64.5 -> "NO4"
            coords.latitude < 59.45 && coords.longitude < 10.5 -> "NO2"
            coords.latitude in 59.3..61.8 && coords.longitude < 8.2 -> "NO5"
            coords.latitude in 61.9..64.5 && coords.longitude < 8.6 -> "NO3"
            else -> "NO1"
        }
    }
}