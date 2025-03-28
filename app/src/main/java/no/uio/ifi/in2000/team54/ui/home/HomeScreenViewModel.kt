package no.uio.ifi.in2000.team54.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.frost.FrostRepository
import no.uio.ifi.in2000.team54.data.pvgis.PVGISRepository


class HomeScreenViewModel: ViewModel() {
    private val _repository = FrostRepository()
    private val _pvgisRepo = PVGISRepository()


    init {
        getObservationsFromRepo()
        getSolarIrradiance()
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

            Log.i("testMapTemp", monthlyTemps.toString())
            Log.i("testMapCloud", monthlyCloud.toString())
            Log.i("testMapSnow", monthlySnow.toString())
            Log.i("testSolarIrradiance", monthlySolarIrradiance.toString())

        }


    }


    // calculating average solar irradiance using PVGIS
    private fun getSolarIrradiance() {
        viewModelScope.launch {
            val monthlyRadiance = _pvgisRepo.getMonthlySolarRadiation(59.9423,10.72, )
            Log.i("testSolar", monthlyRadiance.toString())
        }
    }
}


