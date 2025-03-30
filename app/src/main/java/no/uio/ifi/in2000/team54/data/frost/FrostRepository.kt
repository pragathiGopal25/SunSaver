package no.uio.ifi.in2000.team54.data.frost

import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.model.frost.ObservationData

class FrostRepository() {
    private val datasource: FrostDatasource = FrostDatasource()

    private val nameMap = mapOf(
        "temp" to "mean(air_temperature%20P1M)",
        "cloud" to "mean(cloud_area_fraction%20P1D)",
        "snow" to "mean(snow_coverage_type%20P1M)",
        "radiation" to "mean(surface_downwelling_shortwave_flux_in_air%20PT1H)"
    )

    private val dateInterval = "2022-12-31/2024-12-31"

    // store fetched data from data source in an object
    data class FetchAllData (
        var monthlyTemps: Map<String, Double> = emptyMap(),
        var monthlyCloud: Map<String, Double> = emptyMap(),
        var monthlySnow: Map<String, Double> = emptyMap(),
        var monthlyRadiation: Map<String, Double> = emptyMap()
    )

    suspend fun getObservationData(coordinates: Coordinates): FetchAllData {
        val fetchData = FetchAllData()
        nameMap.forEach { (name, elementName) ->
            when (name) {
                "temp" -> fetchData.monthlyTemps = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
                "cloud" ->  fetchData.monthlyCloud = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
                "snow" -> fetchData.monthlySnow = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
                "radiation" -> fetchData.monthlyRadiation = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
            }
        }

        return fetchData
    }

    // function that calculates and returns monthly average values for the data
    private fun getMonthlyAverageValues (observationList: List<ObservationData>): Map<String, Double>{
        val averageMonthly = mutableMapOf<String,MutableList<Double>>()

        observationList.forEach {
            val month = it.referenceTime.split("-")[1]
            it.observations.forEach { obs -> averageMonthly.getOrPut(month) { mutableListOf() }.add(obs.value) } // extracts month from date
        }

        val monthlyValues = averageMonthly.mapValues { (_, values) -> values.sum() / values.size}

        return monthlyValues.toSortedMap()
    }




}