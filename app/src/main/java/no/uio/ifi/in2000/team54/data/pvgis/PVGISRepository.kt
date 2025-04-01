package no.uio.ifi.in2000.team54.data.pvgis

import no.uio.ifi.in2000.team54.model.pvgis.SolarIrradianceData

class PVGISRepository {
        private val solarData: PVGISDataSource = PVGISDataSource()

        suspend fun getMonthlySolarRadiation(latitude: Double, longitude: Double):Map<String, Double> {
            return getMonthlyAverageValues(solarData.fetchMonthlyRadiation(latitude, longitude))
        }

        // function that calculates and returns monthly average values for the data
        private fun getMonthlyAverageValues (solarRadiationList: List<SolarIrradianceData>): Map<String, Double>{
            val averageMonthly = mutableMapOf<String,MutableList<Double>>()

            val monthConversion = mutableMapOf("Jan" to "01", "Feb" to "02",
                "Mar" to "03", "Apr" to "04", "May" to "05",
                "Jun" to "06", "Jul" to "07", "Aug" to "08",
                "Sep" to "09", "Oct" to "10", "Nov" to "11",
                "Dec" to "12")


            solarRadiationList.forEach {
                var  month = it.month
                month = monthConversion.get(month).toString()
                averageMonthly.getOrPut(month) { mutableListOf() }.add(it.irradiance) } // extracts month from date


            val monthlySolar = averageMonthly.mapValues { (_, values) -> values.sum() / values.size}
            return monthlySolar
        }

}