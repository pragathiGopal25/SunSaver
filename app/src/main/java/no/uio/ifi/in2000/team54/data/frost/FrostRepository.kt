package no.uio.ifi.in2000.team54.data.frost

import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.model.frost.ObservationData

class FrostRepository() {
    private val datasource: FrostDatasource = FrostDatasource()

    private val nameMap = datasource.nameMap

    private val dateInterval = "2022-12-31/2024-12-31"

    // store fetched data from data source in separate objects
    // create an object of the data class, ex tempData
    // the frost API response is to be stores in the variables in this object.

    data class TempData (var monthlyTemps: Map<String, Double> = emptyMap() )
    suspend fun getTempData(coordinates: Coordinates): TempData {

        val data = TempData()
        val temp = nameMap["temp"]

        data.monthlyTemps = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, temp.toString(), dateInterval))
        return data
    }

    data class CloudData (var monthlyCloud: Map<String, Double> = emptyMap() )
    suspend fun getCloudData(coordinates: Coordinates): CloudData {

        val data = CloudData()
        val cloud = nameMap["cloud"]

        data.monthlyCloud = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, cloud.toString(), dateInterval))
        return data
    }

    data class SnowData (var monthlySnow: Map<String, Double> = emptyMap() )
    suspend fun getSnowData(coordinates: Coordinates): SnowData {

        val data = SnowData()
        val snow = nameMap["snow"]

        data.monthlySnow = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, snow.toString(), dateInterval))
        return data
    }

    data class RadiationData (var monthlyRadiation: Map<String, Double> = emptyMap())
    suspend fun getRadiationData(coordinates: Coordinates): RadiationData {

        val data = RadiationData()
        val radiation = nameMap["radiation"]

        data.monthlyRadiation = getMonthlyAverageValues(datasource.fetchObservationDataFromFrost(coordinates, radiation.toString(), dateInterval))
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