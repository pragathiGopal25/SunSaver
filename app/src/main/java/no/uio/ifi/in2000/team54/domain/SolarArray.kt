package no.uio.ifi.in2000.team54.domain

import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.util.calculateSubsidy

data class SolarArray(
    val id: Long?,
    val name: String,
    val panelType: SolarPanelType,
    val roofSections: List<RoofSection>,
    val coordinates: Coordinates,
    val powerConsumption: Double,// kWh per month
    val address: Address?
) {
    fun getTotalPrice(): Double {
        val totalPanels = roofSections.sumOf { it.panels }
        val grossPrice = panelType.totalPrice(totalPanels)
        val subsidy = calculateSubsidy(panelType, totalPanels)
        return grossPrice-subsidy
    }
}
