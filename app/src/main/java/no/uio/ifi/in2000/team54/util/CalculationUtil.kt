package no.uio.ifi.in2000.team54.util

import android.util.Log
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

    Log.i("SolarArrayIs", solarArray.name)
    Log.i("SolarArraySn", monthlyCloud.toString())
    Log.i("SolarArrayCl", monthlySnow.toString())
    Log.i("SolarArrayIr", monthlyRadiance.toString())
    Log.i("SolarArrayTe", monthlyTemperatures.toString())
    Log.i("SolarArraySu", monthlySunhours.toString())


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
            irradiance * sunHours * efficiency * panelArea * roofSection.panels * calculateDirectionImpact(
                roofSection.direction
            ) * calculateAngleImpact(roofSection.incline)
        } / 1000.0 // Convert to kWh
    }
}

//Calculate the efficiency of the solar panel based on temperature in celsius
//If the temperature is more than default (25 deg) the efficiency decreases
private fun calculatePanelEfficiency(temperature: Double, solarArray: SolarArray): Double {
    // Efficiency: https://www.photonicuniverse.com/en/resources/articles/full/7.html
    var efficiency = ((solarArray.panelType.watt)/((solarArray.panelType.length).times(solarArray.panelType.width)))/1000 // divide by thousand to get kW
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
    Log.i("calsSnow", monthlySnow.toString())
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
    // changing this based on: https://www.sunsave.energy/solar-panels-advice/how-solar-works/winter
    // "Light cloud cover typically reduces solar panel output by 24%"
    // "Under heavy cloud cover, your system will produce 67% less electricity, on average."
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

//https://blog.projects4roofing.co.uk/does-roof-orientation-affect-my-solar-panels-efficiency
// efficiency loss if north facing roof = 40%
// optimal direction = 180, so efficiency is 100%.
// efficiency loss per degree = 0.4/180 = 0.002
//Calculates the impact the direction of the panel has on solar panel efficiency
private fun calculateDirectionImpact(azimuth: Double): Double {
    val directionOffset = abs(180 - azimuth) // 180 degrees for perfect south, which is optimal for sunshine in the northern hemisphere
    return maxOf(0.5, 1 - (directionOffset * DIRECTION_EFFICIENCY_DECREASE_PER_DEGREE))
}
