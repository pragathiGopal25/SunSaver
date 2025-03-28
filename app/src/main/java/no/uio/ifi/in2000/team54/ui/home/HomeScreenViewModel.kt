package no.uio.ifi.in2000.team54.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.frost.FrostRepository
import no.uio.ifi.in2000.team54.data.pvgis.PVGISRepository
import kotlin.math.*


class HomeScreenViewModel: ViewModel() {
    private val _repository = FrostRepository()
    private val _pvgisRepo = PVGISRepository()


    init {
        getObservationsFromRepo()
        //getSolarIrradiance()
    }

    private fun getObservationsFromRepo() {
        viewModelScope.launch {
            // retrieves data from the last five years
            val monthlyTemps: Map<String, Double> = _repository.getObservationData(
                59.9423,10.72,  "mean(air_temperature%20P1M)", "2019-01-01/2024-12-31")

            val monthlyCloud: Map<String, Double> = _repository.getObservationData(
                59.9423,10.72,  "mean(cloud_area_fraction%20P1D)", "2019-01-01/2024-12-31")


            val monthlySnow: Map<String, Double> = _repository.getObservationData(
                59.9423,10.72,  "mean(snow_coverage_type%20P1M)", "2019-01-01/2024-12-31")

            // uses frost
            val monthlySolarIrradiance: Map<String, Double> = _repository.getObservationData(
                59.9423,10.72,  "mean(surface_downwelling_shortwave_flux_in_air%20PT1H)", "2019-01-01/2024-12-31")

            val monthlyRadiance = _pvgisRepo.getMonthlySolarRadiation(59.9423,10.72, )

            Log.i("testMapTemp", monthlyTemps.toString())
            Log.i("testMapCloud", monthlyCloud.toString())
            Log.i("testMapSnow", monthlySnow.toString())
            //Log.i("testSolarIrradiance", monthlySolarIrradiance.toString())

            calculateSolarEnergy(
                monthlyTemps = monthlyTemps,
                monthlyCloud = monthlyCloud,
                monthlySnow = monthlySnow,
                monthlyRadiance = monthlyRadiance,
                area = 20.0,
                angle= 35.0,
                direction = 180.0,
                latitude = 59.0
            )

        }


    }


    // calculating average solar irradiance using PVGIS
    private fun getSolarIrradiance() {
        viewModelScope.launch {
            val monthlyRadiance = _pvgisRepo.getMonthlySolarRadiation(59.9423,10.72, )
            Log.i("testSolar", monthlyRadiance.toString())
        }
    }

    // https://chatgpt.com/share/67e6bf32-a850-8008-ba21-1b8d5e6303b9
    private fun calculateSolarEnergy(
        monthlyTemps: Map<String, Double>,
        monthlyCloud: Map<String, Double>,
        monthlySnow: Map<String, Double>,
        monthlyRadiance: Map<String, Double>,
        area: Double,
        angle: Double,
        direction: Double,
        latitude: Double
    ): Map<String, Double> {
        val result = mutableMapOf<String, Double>()

        val optimalTilt = latitude * 0.76 + 3.1
        val tiltFactor = cos(Math.toRadians(angle - optimalTilt))
        val orientationFactor = orientationFactor(direction.toInt())

        val referenceEfficiency = 0.20
        val temperatureCoefficient = 0.004

        for (month in monthlyRadiance.keys) {
            val ghi = monthlyRadiance[month] ?: continue
            val cloud = monthlyCloud[month] ?: 0.0  // 0.3 = 30% skydekke
            val snow = monthlySnow[month] ?: 0.0    // 0.1 = 10% snødekket
            val temp = monthlyTemps[month] ?: 0.0   // °C

            val poa = ghi * tiltFactor * orientationFactor * (1 - cloud) * (1 - snow)

            // Enkel modell for celletemperatur: omgivelsestemp + ca. 20 °C
            val tCell = temp + 20
            val actualEfficiency = referenceEfficiency * (1 - temperatureCoefficient * (tCell - 25))

            val energy = poa * area * actualEfficiency  // kWh for måneden
            result[month] = energy
            Log.i("FFFFF", result.toString())
        }
        Log.i("DONERESULT", result.toString())
        return result
    }

    private fun orientationFactor(azimuth: Int): Double {
        return when (azimuth) {
            in 170..190 -> 1.00
            in 140..210 -> 0.98
            in 110..240 -> 0.95
            in 80..100, in 260..280 -> 0.90
            in 50..110, in 250..310 -> 0.80
            in 20..50, in 310..340 -> 0.65
            else -> 0.50
        }
    }

}


