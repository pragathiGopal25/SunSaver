package no.uio.ifi.in2000.team54.data.pvgis

import android.util.Log
import no.uio.ifi.in2000.team54.model.pvgis.SolarIrradianceData

class PVGISRepository {

        private val solarData: PVGISDataSource = PVGISDataSource()


        suspend fun getMonthlySolarRadiation(latitude: Double, longitude: Double):Map<String, Double> {
            return getMonthlyAverageValues(solarData.fetchMonthlyRadiation(latitude, longitude))
        }

        // function that calculates and returns monthly average values for the data
        private fun getMonthlyAverageValues (solarRadiationList: List<SolarIrradianceData>): Map<String, Double>{
            val averageMonthly = mutableMapOf<String,MutableList<Double>>()

            solarRadiationList.forEach {
                val month = it.month
                averageMonthly.getOrPut(month) { mutableListOf() }.add(it.irradiance) } // extracts month from date


            val monthlyTemps = averageMonthly.mapValues { (_, values) -> values.sum() / values.size}
            return monthlyTemps.toSortedMap()
        }

}