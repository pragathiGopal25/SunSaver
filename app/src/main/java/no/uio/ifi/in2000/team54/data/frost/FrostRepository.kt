package no.uio.ifi.in2000.team54.data.frost

import android.util.Log
import no.uio.ifi.in2000.team54.model.frost.ObservationData

class FrostRepository() {
    private val datasource: FrostDatasource = FrostDatasource()

    // Just to test connection
    suspend fun getSomethingFromDatasource(latitude: Double, longitude: Double): List<String> {
        return datasource.getSomethingFromFrost(latitude, longitude)
    }

    // get observation data
    suspend fun getObservationData(latitude: Double, longitude: Double,elementName: String, referenceTime:String): Map<String, Double> {
       return getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(latitude, longitude, elementName, referenceTime))
    }

    // function that calculates and returns monthly average values for the data
    private fun getMonthlyAverageValues (observationList: List<ObservationData>): Map<String, Double>{
        val averageMonthly = mutableMapOf<String,MutableList<Double>>()

        observationList.forEach {
            val month = it.referenceTime.split("-")[1]
            it.observations.forEach { obs -> averageMonthly.getOrPut(month) { mutableListOf() }.add(obs.value) } // extracts month from date
        }

        val monthlyTemps = averageMonthly.mapValues { (_, values) -> values.sum() / values.size}

        return monthlyTemps.toSortedMap()
    }
}