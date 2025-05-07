package no.uio.ifi.in2000.team54.data.frost

import android.util.Log
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.enums.Elements
import no.uio.ifi.in2000.team54.model.frost.ObservationData
import javax.inject.Inject

class FrostRepository @Inject constructor(private val datasource: FrostDatasource) {

    // store fetched data from data source in separate objects
    // create an object of the data class, ex tempData
    // the frost API response is to be stores in the variables in this object.

    suspend fun getData( coordinates: Coordinates, elementName: Elements ): Map<String, Double> {

        return getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName))
    }

    // function that calculates and returns monthly average values for the data
    private fun getMonthlyAverageValues (observationList: List<ObservationData>): Map<String, Double> {

        val averageMonthly = mutableMapOf<String, MutableList<Double>>()

        // note that each month here is stores as "01", "02" etc and not as "Jan", "Feb" ...
        observationList.forEach {
            val month = it.referenceTime.split("-")[1]
            it.observations.forEach { obs ->
                averageMonthly.getOrPut(month) { mutableListOf() }.add(obs.value)
            } // extracts month from date
        }

        val monthlyValues = averageMonthly.mapValues { (_, values) -> values.sum() / values.size }

        return monthlyValues.toSortedMap()
    }
}