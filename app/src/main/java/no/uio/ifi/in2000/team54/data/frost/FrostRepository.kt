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

    // store fetched data from data source in separate objects

    data class tempData( var monthlyTemps: Map<String, Double> = emptyMap() )
    suspend fun getTempData(coordinates: Coordinates): tempData {
        // creates an object of the data class tempData
        // the frost API response is to be stores in the variables in this object.
        val data = tempData()
        nameMap.forEach { (name, elementName) ->
            when (name) {
                "temp" -> data.monthlyTemps = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
            }
        }
        return data
    }

    data class cloudData( var monthlyCloud: Map<String, Double> = emptyMap() )
    suspend fun getCloudData(coordinates: Coordinates): cloudData {
        val data = cloudData()
        nameMap.forEach { (name, elementName) ->
            when (name) {
                "cloud" -> data.monthlyCloud = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
            }
        }
        return data
    }

    data class snowData( var monthlySnow: Map<String, Double> = emptyMap() )
    suspend fun getSnowData(coordinates: Coordinates): snowData {
        val data = snowData()
        nameMap.forEach { (name, elementName) ->
            when (name) {
                "snow" -> data.monthlySnow = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
            }
        }
        return data
    }

    data class radiationData( var monthlyRadiation: Map<String, Double> = emptyMap())
    suspend fun getRadiationData(coordinates: Coordinates): radiationData {
        val data = radiationData()
        nameMap.forEach { (name, elementName) ->
            when (name) {
                "radiation" -> data.monthlyRadiation = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, elementName, dateInterval))
            }
        }
        return data
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