package no.uio.ifi.in2000.team54.util

import no.uio.ifi.in2000.team54.enums.SolarPanelType

const val BASE_SUBSIDY = 7500.0
const val PER_KW_SUBSIDY = 1250.0

fun calculateSubsidy(panelType: SolarPanelType, amount: Int): Double {
    if (amount < 1) {
        return 0.0
    }

    return panelType.watt / 1000.0 * PER_KW_SUBSIDY * amount + BASE_SUBSIDY
}