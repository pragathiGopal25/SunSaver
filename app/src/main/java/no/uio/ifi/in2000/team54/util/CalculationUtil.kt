package no.uio.ifi.in2000.team54.util

import android.util.Log
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import kotlin.math.abs

fun calculateElectrisityProduction(
    monthlyCloud: Map<String, Double>,
    monthlySnow: Map<String, Double>,
    monthlyRadiance: Map<String, Double>,
    monthlyTemps: Map<String, Double>,
    solarArray: SolarArray
): Map<String, Double>  {
    val adjustedIrradiance: Map<String, Double> = calculateSolarEnergy(
        monthlyCloud = monthlyCloud,
        monthlySnow = monthlySnow,
        monthlyRadiance = monthlyRadiance
    )
    val resultMap = mutableMapOf<String, Double>()
    // need to calculate per roofSection and sum up
    // factors to consider: paneltype (watt), area, incline, direction, temperature // todo: use watts somewhere

    val roofSections: List<RoofSection> = solarArray.roofSections

    adjustedIrradiance.keys.forEach {
        val irradiance = adjustedIrradiance[it] ?: 0.0 // W/m2
        val sunHours = monthlySunHoursAvg[it] ?: 0.0

        val temperature = monthlyTemps[it] ?: 25.0
        var virkningsgrad = 0.20 // average value, have to research how to adjust depending on solarpaneltype // todo
        // https://snl.no/solceller
        val temperaturKoeff = 0.5 // percent loss per grad Celsius
        if (temperature > 25.0) {
            val overTemp = temperature - 25.0
            val tapProsent = overTemp * temperaturKoeff
            virkningsgrad -= virkningsgrad * (tapProsent / 100.0)
        }

        // calculate the sum for roofSections
        var resultEnergy = 0.0
        roofSections.forEach { roofSec ->
            resultEnergy += irradiance * sunHours* virkningsgrad * roofSec.area * directionImpact(roofSec.direction) * angleImpact(roofSec.incline)
        }
        resultMap[it] = resultEnergy/1000
    }

    return resultMap
}

private fun calculateSolarEnergy( // beregne gjennomsnittlig forventet innkommende solenergi basert på influx, posisjon, skydekke og snø
    monthlyCloud: Map<String, Double>,
    monthlySnow: Map<String, Double>,
    monthlyRadiance: Map<String, Double>,
): Map<String, Double> {
    val result = mutableMapOf<String, Double>()
    monthlyRadiance.keys.forEach {
        var irradiance = monthlyRadiance[it] ?: 0.0

        // calculate snow factor
        val snowFactor: Double? = monthlySnow[it]?.let { snowLoss(it) }
        if (snowFactor != null) {
            irradiance = irradiance.times(snowFactor)
        }

        // calculate cloud factor
        val cloudFactor: Double? = monthlyCloud[it]?.let { cloudLoss(it) }
        if (cloudFactor != null) {
            irradiance = irradiance.times(cloudFactor)
        }

        result[it] = irradiance

    }
    Log.i("testRes", result.toString())
    return result
}

private fun snowLoss(snowCoverage: Double): Double {
    return when (snowCoverage) {
        1.0 -> 0.90
        2.0 -> 0.70
        3.0 -> 0.40
        4.0 -> 0.10
        else -> 1.0
    }
}

private fun cloudLoss(cloudCover: Double): Double {
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
private fun angleImpact(angle: Double): Double {
    if (angle in 15.0..45.0) {
        return 1.0
    }
    val diff = if (angle < 15) {
        15 - angle
    } else {
        angle - 45
    }
    return maxOf(0.5, 1 - (diff * 0.05))
}

// todo: also can be adjusted
private fun directionImpact(azimuth: Double): Double {
    val diff = abs(180 - azimuth)
    return maxOf(0.5, 1 - (diff * 0.005))
}

private val monthlySunHoursAvg = mapOf( // todo: Get from datasource!!
    "01" to 20.0,
    "02" to 50.0,
    "03" to 100.0,
    "04" to 140.0,
    "05" to 180.0,
    "06" to 200.0,
    "07" to 190.0,
    "08" to 160.0,
    "09" to 110.0,
    "10" to 70.0,
    "11" to 30.0,
    "12" to 15.0
)