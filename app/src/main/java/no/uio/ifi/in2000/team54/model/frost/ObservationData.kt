package no.uio.ifi.in2000.team54.model.frost

import kotlinx.serialization.Serializable

@Serializable
data class ObservationData(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<Observation>
)

@Serializable
data class Observation(
    val elementId: String,
    val value: Double,
    val unit: String,
    val timeOffset: String,
    val timeResolution: String,
    val timeSeriesId: Double,
    val performanceCategory: String,
    val exposureCategory: String,
    val qualityCode: Double,
)




@Serializable
data class AvailableObservationResponse(
    val data: List<AvailableObservation> // This maps the "data" field in the JSON
)

@Serializable
data class AvailableObservation(
    val sourceId: String,
    val validFrom: String,
    val timeOffset: String,
    val timeResolution: String,
    val timeSeriesId: Int,
    val elementId: String,
    val unit: String
)


@Serializable
data class ObservationResponse(
    val data: List<ObservationData>
)
