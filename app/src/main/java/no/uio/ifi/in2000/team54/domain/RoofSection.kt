package no.uio.ifi.in2000.team54.domain

data class RoofSection(
    val area: Double,
    val incline: Double,
    val direction: Double,
    val panels: Int,
    val mapId: String?,
)