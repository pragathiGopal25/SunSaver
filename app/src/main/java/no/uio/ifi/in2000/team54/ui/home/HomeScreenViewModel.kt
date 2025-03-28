package no.uio.ifi.in2000.team54.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.frost.FrostRepository


class HomeScreenViewModel: ViewModel() {
    private val _repository = FrostRepository()

    // just to test connection
    init {
        getSomethingFromRepository()
        getObservationsFromRepo()
    }
    private fun getSomethingFromRepository() {
        viewModelScope.launch {
            val list = _repository.getSomethingFromDatasource(10.72, 59.9423)
            Log.i("test", list.toString())
        }
    }

    private fun getObservationsFromRepo() {
        viewModelScope.launch {
            // retrieves data from the last five years
            val monthlyTemps: Map<String, Double> = _repository.getObservationData(
                10.72, 59.9423, "mean(air_temperature%20P1M)", "2019-01-01/2024-12-31")

            val monthlyCloud: Map<String, Double> = _repository.getObservationData(
                10.72, 59.9423, "mean(cloud_area_fraction%20P1D)", "2019-01-01/2024-12-31")


            val monthlySnow: Map<String, Double> = _repository.getObservationData(
                10.72, 59.9423, "mean(snow_coverage_type%20P1M)", "2019-01-01/2024-12-31")

            /*val sunshineHoursList: List<ObservationData> = _repository.getObservationData(
                "10.72", "59.9423", "mean(air_temperature%20P1M)", "2019-12-30/2024-12-30")*/


            Log.i("testMapTemp", monthlyTemps.toString())
            Log.i("testMapCloud", monthlyCloud.toString())
            Log.i("testMapSnow", monthlySnow.toString())

        }


    }
}


