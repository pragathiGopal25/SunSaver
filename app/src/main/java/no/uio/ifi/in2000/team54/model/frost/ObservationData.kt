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
data class ObservationResponse(
    val data: List<ObservationData>
)
