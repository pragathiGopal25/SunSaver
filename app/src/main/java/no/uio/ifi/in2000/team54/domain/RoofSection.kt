package no.uio.ifi.in2000.team54.domain

data class RoofSection(
    val id: Int?,
    val area: Double,
    val incline: Double,
    val direction: Double,
    val panels: Int,
    val mapId: String?,
)