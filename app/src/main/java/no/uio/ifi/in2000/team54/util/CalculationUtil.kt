package no.uio.ifi.in2000.team54.util

import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import kotlin.math.abs

//Constants used in calculations
//This makes the code easier to maintain and read if we tweak the calculations
private const val MINIMUM_OPTIMAL_INCLINE_ANGLE = 15.0 //Degrees
private const val MAXIMUM_OPTIMAL_INCLINE_ANGLE = 45.0 //Degrees
private const val INCLINE_ANGLE_EFFICIENCY_DECREASE_PER_DEGREE = 0.05
private const val DIRECTION_EFFICIENCY_DECREASE_PER_DEGREE = 0.002
private const val DEFAULT_PANEL_TEMPERATURE_CELSIUS = 25.0
private const val TEMPERATURE_EFFICIENCY_LOSS_PER_CELSIUS = 0.25 // Percentage loss per degree Celsius
private const val TEMPERATURE_EFFICIENCY_GAIN_PER_CELSIUS = 0.4


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

        roofSections.sumOf { roofSection ->
            irradiance * sunHours * efficiency * panelArea * roofSection.panels * calculateDirectionImpact(
                roofSection.direction
            ) * calculateAngleImpact(roofSection.incline)
        } / 1000// Convert to kWh
    }
}

//Calculate the efficiency of the solar panel based on temperature in celsius
// temperature gain: https://www.bostonsolar.us/solar-blog-resource-center/blog/how-do-temperature-and-shade-affect-solar-panel-efficiency/
private fun calculatePanelEfficiency(temperature: Double, solarArray: SolarArray): Double {
    // Efficiency: https://www.photonicuniverse.com/en/resources/articles/full/7.html
    val panelArea = solarArray.panelType.length.times(solarArray.panelType.width)
    var efficiency = ((solarArray.panelType.watt)/(panelArea * 1000))// divide by thousand to get kW
    // if temperature is more than 25 degrees, we decrease efficiency by the temperature coefficient
    if (temperature > DEFAULT_PANEL_TEMPERATURE_CELSIUS) {
        val temperatureDifference = temperature - DEFAULT_PANEL_TEMPERATURE_CELSIUS
        val efficiencyLossPercentage = temperatureDifference * TEMPERATURE_EFFICIENCY_LOSS_PER_CELSIUS
        efficiency -= efficiency * (efficiencyLossPercentage / 100.0)
    }  else if (temperature < DEFAULT_PANEL_TEMPERATURE_CELSIUS) {
     // Efficiency increases when the temperature is lower, so we make sure to add on to it here.
        val temperatureDifference = DEFAULT_PANEL_TEMPERATURE_CELSIUS - temperature
        val efficiencyGainPercentage = temperatureDifference * TEMPERATURE_EFFICIENCY_GAIN_PER_CELSIUS
        efficiency += efficiency * (efficiencyGainPercentage / 100.0)
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
        val snowFactor = monthlySnow[month]?.let { calculateSnowLossFactor(it, month)} ?: 1.0
        val cloudFactor = monthlyCloud[month]?.let { calculateCloudLossFactor(it) } ?: 1.0
        adjustedIrradiance *= snowFactor * cloudFactor
        adjustedIrradiance
    }
}

//Calculates the impact the snow has on solar panel efficiency
//https://www.sciencedirect.com/science/article/abs/pii/S1364032118308268
// 25 % less efficiency of solar panels in winter months
private fun calculateSnowLossFactor(snowCoverage: Double, month: String): Double {
    val isWinter = month in listOf("11", "12", "01", "02") // winter months, efficiency is lower
    return when (snowCoverage) {
         1.0 -> if (isWinter) 0.92 else 0.98
         2.0 -> if (isWinter) 0.90 else 0.96
         3.0 -> if (isWinter) 0.80 else 0.93
         4.0 -> if (isWinter) 0.75 else 0.90
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
        7 -> 0.65
        8 -> 0.60
        else -> 0.75 // Fallback for unknown
    }
}

// https://www.otovo.no/blog/solcellepanel-solceller/solceller-norge-virkningsgrad/#sollys-og-innfallsvinkelhelling-av-solceller
//Calculates the impact the angle of the panel has on solar panel efficiency
private fun calculateAngleImpact(inclineAngle: Double): Double {
    var tempInclineAngle = inclineAngle
    if (tempInclineAngle < 5){ // for flat roofs solar panels are installed at an angle of 10 degrees
        tempInclineAngle = 10.0
    }
    if (tempInclineAngle in MINIMUM_OPTIMAL_INCLINE_ANGLE..MAXIMUM_OPTIMAL_INCLINE_ANGLE) {
        return 1.0
    }
    val angleOffset = if (tempInclineAngle < MINIMUM_OPTIMAL_INCLINE_ANGLE) {
        MINIMUM_OPTIMAL_INCLINE_ANGLE - tempInclineAngle
    } else {
        tempInclineAngle - MAXIMUM_OPTIMAL_INCLINE_ANGLE
    }
    return maxOf(0.5, 1 - (angleOffset * INCLINE_ANGLE_EFFICIENCY_DECREASE_PER_DEGREE))
}

//https://blog.projects4roofing.co.uk/does-roof-orientation-affect-my-solar-panels-efficiency
// efficiency loss if north facing roof = 40%
// optimal direction = 180, so efficiency is 100%.
// efficiency loss per degree = 0.4/180 = 0.002
//Calculates the impact the direction of the panel has on solar panel efficiency
private fun calculateDirectionImpact(azimuth: Double): Double {
    val directionOffset = abs(180 - azimuth) // 180 degrees for perfect south, which is optimal for sunshine in the northern hemisphere
    return maxOf(0.5, 1 - (directionOffset * DIRECTION_EFFICIENCY_DECREASE_PER_DEGREE))
}
