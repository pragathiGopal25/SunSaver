package no.uio.ifi.in2000.team54.domain

import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.model.building.Address

data class SolarArray(
    val id: Long?,
    val name: String,
    val panelType: SolarPanelType,
    val roofSections: List<RoofSection>,
    val coordinates: Coordinates,
    val powerConsumption: Double,// kWh per month
    val address: String
)