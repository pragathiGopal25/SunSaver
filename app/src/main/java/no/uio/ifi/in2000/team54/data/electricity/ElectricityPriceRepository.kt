package no.uio.ifi.in2000.team54.data.electricity

class ElectricityPriceRepository(datasource: ElectricityPriceDatasource) {
    suspend fun getNokKwh():List<Double> {
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
        val selfProducedElectiricty = 0.1
        return (getNokKwh().average() - selfProducedElectiricty)
    }

    suspend fun absPrice() = fakeAvgKwh() * avgNokKwh()
    suspend fun absPriceSolar() = fakeAvgKwh() * fakeSolarPriceData()

    private fun fakeAvgKwh(): Double = 70.6 //Enebolig kWh produsert daglig avg
}