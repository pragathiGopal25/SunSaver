package no.uio.ifi.in2000.team54.model.frost

import kotlinx.serialization.Serializable

@Serializable
data class SourceResponse(
    val data: List<SensorSystem>
)
