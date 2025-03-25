package no.uio.ifi.in2000.team54.model.frost

import kotlinx.serialization.Serializable

@Serializable
data class SensorSystem(
    val id: String,
    val name: String,
    val shortName: String,
    val geometry: SystemGeometry,
    val distance: Double,
    val validFrom: String,
)

@Serializable
data class SystemGeometry(
    val coordinates: List<Double>,
    val nearest: Boolean
)