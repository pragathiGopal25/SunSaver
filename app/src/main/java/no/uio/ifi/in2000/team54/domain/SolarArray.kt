package no.uio.ifi.in2000.team54.domain

import no.uio.ifi.in2000.team54.enums.SolarPanelType

data class SolarArray(
    val name: String,
    val panelType: SolarPanelType,
    val roofSections: List<RoofSection>,
    val coordinates: Coordinates,
    val powerConsumption: Double // kWh per month
)