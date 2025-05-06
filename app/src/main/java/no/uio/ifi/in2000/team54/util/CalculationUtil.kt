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
private const val DEFAULT_PANEL_EFFICIENCY = 0.20
private const val TEMPERATURE_EFFICIENCY_LOSS_PER_CELSIUS = 0.5 // Percentage loss per degree Celsius

//Average hours of sun per month in Norway
private val avgMonthlySunHours = mapOf( // todo: Get from datasource!!
    "01" to 74.0,
    "02" to 90.0,
    "03" to 150.0,
    "04" to 200.0,
    "05" to 210.0,
    "06" to 230.0,
    "07" to 210.0,
    "08" to 180.0,
    "09" to 150.0,
    "10" to 90.0,
    "11" to 60.0,
    "12" to 55.0
)

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

    // factors to consider: paneltype (watt), area, incline, direction, temperature // todo: use watts somewhere
    return monthlyIrradiance.mapValues { (month, irradiance) ->
        val sunHours = avgMonthlySunHours[month] ?: 0.0
        val temperature = monthlyTemperatures[month] ?: DEFAULT_PANEL_TEMPERATURE_CELSIUS
        val efficiency = calculatePanelEfficiency(temperature)

        // need to calculate per roofSection and sum up
        roofSections.sumOf { roofSection ->
            irradiance * sunHours * efficiency * roofSection.area * calculateDirectionImpact(
                roofSection.direction
            ) * calculateAngleImpact(roofSection.incline)
        } / 1000.0 // Convert to kWh
    }
}

//Calculate the efficiency of the solar panel based on temperature in celsius
//If the temperature is more than default (25 deg) the efficiency decreases
private fun calculatePanelEfficiency(temperature: Double): Double {
    var efficiency = DEFAULT_PANEL_EFFICIENCY
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
    return when (cloudCover.toInt()) {
        0 -> 1.00
        1 -> 0.97
        2 -> 0.93
        3 -> 0.88
        4 -> 0.82
        5 -> 0.75
        6 -> 0.68
        7 -> 0.60
        8 -> 0.50
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
    val directionOffset = abs(180 - azimuth)
    return maxOf(0.5, 1 - (directionOffset * DIRECTION_EFFICIENCY_DECREASE_PER_DEGREE))
}
