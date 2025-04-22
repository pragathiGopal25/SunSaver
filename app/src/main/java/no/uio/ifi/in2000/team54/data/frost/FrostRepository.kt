package no.uio.ifi.in2000.team54.data.frost

import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.enums.Elements
import no.uio.ifi.in2000.team54.model.frost.ObservationData

class FrostRepository() {
    private val datasource: FrostDatasource = FrostDatasource()

    private val dateInterval = "2022-12-31/2024-12-31"

    // store fetched data from data source in separate objects
    // create an object of the data class, ex tempData
    // the frost API response is to be stores in the variables in this object.

    suspend fun getData( coordinates: Coordinates, elementName: Elements ): Map<String, Double> {

        return getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
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