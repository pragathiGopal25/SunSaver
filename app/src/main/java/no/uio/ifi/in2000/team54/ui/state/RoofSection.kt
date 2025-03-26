package no.uio.ifi.in2000.team54.ui.state

import no.uio.ifi.in2000.team54.enums.SolarPanelType

class RoofSection(
    val area: Double,
    val incline: Double,
    val direction: Double,
    val solarPanelType: SolarPanelType,
    val mapId: String?,
)