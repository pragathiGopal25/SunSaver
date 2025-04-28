package no.uio.ifi.in2000.team54.util

import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import kotlin.math.abs

//Constants used in calculations
//This makes the code easier to maintain and read if we tweak the calculations
private const val MINIMUM_OPTIMAL_INCLINE_ANGLE = 15.0 //Degrees
private const val MAXIMUM_OPTIMAL_INCLINE_ANGLE = 45.0 //Degrees
private const val INCLINE_ANGLE_EFFICIENCY_DECREASE_PER_DEGREE = 0.05
private const val DIRECTION_EFFICIENCY_DECREASE_PER_DEGREE = 0.005
private const val DEFAULT_PANEL_TEMPERATURE_CELSIUS = 25.0
private const val TEMPERATURE_EFFICIENCY_LOSS_PER_CELSIUS = 0.5 // Percentage loss per degree Celsius


//Calculates the monthly electricity production for a solar array based on set factors
fun calculateMonthlyElectricityProduction(
    monthlyCloud: Map<String, Double>,
    monthlySnow: Map<String, Double>,
    monthlyRadiance: Map<String, Double>,
    monthlyTemperatures: Map<String, Double>,
    monthlySunhours: Map<String, Double>,
    solarArray: SolarArray
): Map<String, Double> {
    val monthlyIrradiance = calculateAdjustedSolarIrradiance(monthlyCloud, monthlySnow, monthlyRadiance)
    val roofSections: List<RoofSection> = solarArray.roofSections
    val panelArea = solarArray.panelType.length.times(solarArray.panelType.width)

    // factors to consider: paneltype (watt), area, incline, direction, temperature
    return monthlyIrradiance.mapValues { (month, irradiance) ->
        val sunHours = monthlySunhours[month] ?: 0.0
        val temperature = monthlyTemperatures[month] ?: DEFAULT_PANEL_TEMPERATURE_CELSIUS
        val efficiency = calculatePanelEfficiency(temperature, solarArray)


        // substitute roofSection.area with panelArea.time(roofsection.panel)
        // need to calculate per roofSection and sum up
        roofSections.sumOf { roofSection ->
            irradiance * sunHours * efficiency * (panelArea.times(roofSection.panels)) * calculateDirectionImpact(
                roofSection.direction
            ) * calculateAngleImpact(roofSection.incline)
        } / 1000.0 // Convert to kWh
    }
}

//Calculate the efficiency of the solar panel based on temperature in celsius
//If the temperature is more than default (25 deg) the efficiency decreases
private fun calculatePanelEfficiency(temperature: Double, solarArray: SolarArray): Double {
    // Efficiency: https://www.photonicuniverse.com/en/resources/articles/full/7.html
    var efficiency = ((solarArray.panelType.watt)/((solarArray.panelType.length).times(solarArray.panelType.width)))
    if (temperature > DEFAULT_PANEL_TEMPERATURE_CELSIUS) {
        val temperatureDifference = temperature - DEFAULT_PANEL_TEMPERATURE_CELSIUS
        val efficiencyLossPercentage = temperatureDifference * TEMPERATURE_EFFICIENCY_LOSS_PER_CELSIUS
        efficiency -= efficiency * (efficiencyLossPercentage / 100.0)
    }
    return efficiency
}

// Calculate and adjust average solar irradiance based on cloud cover and snow
private fun calculateAdjustedSolarIrradiance(
    monthlyCloud: Map<String, Double>,
    monthlySnow: Map<String, Double>,
    monthlyRadiance: Map<String, Double>,
): Map<String, Double> {

    return monthlyRadiance.mapValues { (month, radiance) ->
        var adjustedIrradiance = radiance
        val snowFactor = monthlySnow[month]?.let { calculateSnowLossFactor(it) } ?: 1.0
        val cloudFactor = monthlyCloud[month]?.let { calculateCloudLossFactor(it) } ?: 1.0
        adjustedIrradiance *= snowFactor * cloudFactor
        adjustedIrradiance
    }
}

//Calculates the impact the snow has on solar panel efficiency
private fun calculateSnowLossFactor(snowCoverage: Double): Double {
    return when (snowCoverage) {
        1.0 -> 0.98
        2.0 -> 0.96
        3.0 -> 0.93
        4.0 -> 0.90
        else -> 1.0
    }
}

//Calculates the impact the cloud cover has on solar panel efficiency
private fun calculateCloudLossFactor(cloudCover: Double): Double {
    // changing this based on: https://www.sunsave.energy/solar-panels-advice/how-solar-works/winter
    // "Light cloud cover typically reduces solar panel output by 24%"
    // "Under heavy cloud cover, your system will produce 67% less electricity, on average."
    return when (cloudCover.toInt()) {
        0 -> 1.00
        1 -> 0.86
        2 -> 0.76 // light cloud - 24% of the actual output
        3 -> 0.65
        4 -> 0.55
        5 -> 0.45
        6 -> 0.40
        7 -> 0.35
        8 -> 0.33 // heavy cloud, - 67% of the actual output
        else -> 0.75 // Fallback for unknown
    }
}

// https://www.otovo.no/blog/solcellepanel-solceller/solceller-norge-virkningsgrad/#sollys-og-innfallsvinkelhelling-av-solceller
// todo: can be adjusted
//Calculates the impact the angle of the panel has on solar panel efficiency
private fun calculateAngleImpact(inclineAngle: Double): Double {
    if (inclineAngle in MINIMUM_OPTIMAL_INCLINE_ANGLE..MAXIMUM_OPTIMAL_INCLINE_ANGLE) {
        return 1.0
    }
    val angleOffset = if (inclineAngle < MINIMUM_OPTIMAL_INCLINE_ANGLE) {
        MINIMUM_OPTIMAL_INCLINE_ANGLE - inclineAngle
    } else {
        inclineAngle - MAXIMUM_OPTIMAL_INCLINE_ANGLE
    }
    return maxOf(0.5, 1 - (angleOffset * INCLINE_ANGLE_EFFICIENCY_DECREASE_PER_DEGREE))
}

// todo: also can be adjusted
//Calculates the impact the direction of the panel has on solar panel efficiency
private fun calculateDirectionImpact(azimuth: Double): Double {
    val directionOffset = abs(180 - azimuth) // 180 degrees for perfect south, which is optimal for sunshine in the northern hemisphere
    return maxOf(0.5, 1 - (directionOffset * DIRECTION_EFFICIENCY_DECREASE_PER_DEGREE))
}
