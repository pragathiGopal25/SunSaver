package no.uio.ifi.in2000.team54.data.electricity

class ElectricityPriceRepository(datasource: ElectricityPriceDatasource) {
    private suspend fun getNokKwh(): List<Double> {
        val nokPerKwh: MutableList<Double> = mutableListOf()

        val data = ElectricityPriceDatasource().getTodaysElectricityPrices("NO1")
        data.forEach {
            nokPerKwh.add(it.nokPrKiloWh)
        }

        return nokPerKwh
    }

    suspend fun avgNokKwh(): Double {
        return getNokKwh().average()
    }

    suspend fun fakeSolarPriceData(): Double {
        val selfProducedElectricity = 0.1
        return (getNokKwh().average() - selfProducedElectricity)
    }

    suspend fun absPrice() = fakeAvgKwh() * avgNokKwh()
    suspend fun absPriceSolar() = fakeAvgKwh() * fakeSolarPriceData()

    private fun fakeAvgKwh(): Double = 70.6 //Enebolig kWh produsert daglig avg

    suspend fun absPriceMonth(start: String, end: String, area: String) =
        fakeAvgKwh() * getPriceDataInterval(start, end, area).average()

    suspend fun getPriceDataInterval(
        start: String,
        end: String,
        area: String,
    ): List<Double> {
        val nokPerKwh: MutableList<Double> = mutableListOf()
        var currentDate = start

        while (currentDate != end) {
            currentDate = incrementDate(currentDate)
            ElectricityPriceDatasource().getElectricityPrices(area, currentDate).forEach {
                nokPerKwh.add(it.nokPrKiloWh)
            }
        }
        return nokPerKwh
    }

    private fun incrementDate(date: String): String {
        val months = mapOf(
            "01" to "31", "02" to "28", "03" to "31", "04" to "30",
            "05" to "31", "06" to "30", "07" to "31", "08" to "31", "09" to "30",
            "10" to "31", "11" to "30", "12" to "31"
        )

        fun increment(value: String): String = (value.toInt() + 1).toString()
        val info = date.split("-", "/")
        var year = info[0]
        var month = info[1]
        var day = info[2]

        day = increment(day)
        if (day.length < 2) day = "0$day"
        if (month == "02" && year.toInt() % 4 == 0 && (year.toInt() % 100 != 0 || year.toInt() % 400 == 0)) {
            //include leap years and don't skip feb 29
        } else if (months[month]!! < day) {
            month = increment(month)
            if (month.length < 2) month = "0$month"
            day = "01"
        }
        if (month == "12" && day > "31") {
            year = increment(year)
            month = "01"
            day = "01"
        }

        val newDate = "$year/$month-$day"
        return newDate
    }
}