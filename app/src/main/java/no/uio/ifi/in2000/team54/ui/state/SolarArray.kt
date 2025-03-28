package no.uio.ifi.in2000.team54.ui.state

import no.uio.ifi.in2000.team54.enums.SolarPanelType

class SolarArray(
    val name: String,
    val panelType: SolarPanelType,
    val roofSections: List<RoofSection>
)