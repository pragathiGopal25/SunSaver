package no.uio.ifi.in2000.team54.data.electricity

class ElectricityPriceRepository(datasource: ElectricityPriceDatasource) {
    suspend fun getNokKwh():List<Double> {
        val nokPerKwh: MutableList<Double> = mutableListOf()

        val data = ElectricityPriceDatasource().getElectricityPrices()
        data.forEach {
            nokPerKwh.add(it.nokPrKiloWh)
        }

        return nokPerKwh
    }
    suspend fun avgNokKwh(): Double {
        return getNokKwh().average()
    }

    suspend fun fakeSolarPriceData(): Double {
        val selfProducedElectiricty: Double = 0.05
        return (getNokKwh().average() - selfProducedElectiricty)
    }
}