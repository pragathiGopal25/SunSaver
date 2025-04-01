package no.uio.ifi.in2000.team54.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.frost.FrostRepository
import no.uio.ifi.in2000.team54.data.pvgis.PVGISRepository
import no.uio.ifi.in2000.team54.data.shared.SharedRepository
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.util.calculateElectrisityProduction

data class GraphDataUiState(
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val loadingState: String = "Henter data... Det kan ta tid"
)

data class SolarArrayUiState( // i guess we fetch separately for different addresses
    val arrays: List<SolarArray> = emptyList(),
    val solarArrayInFocus: SolarArray? = null
)

class HomeScreenViewModel: ViewModel() {
    private val _repository = FrostRepository()
    private val _pvgisRepo = PVGISRepository() // probably will delete later
    private val _sharedRepository = SharedRepository()

    private lateinit var fetchedData: FrostRepository.FetchAllData

    private val _graphDataUiState = MutableStateFlow(GraphDataUiState())
    private val _solarArrayUiState = MutableStateFlow(SolarArrayUiState())

    val graphDataUiState = _graphDataUiState.asStateFlow()

    init {
        getAllSolarArrays()
        getObservationsFromRepo(solarArray = _solarArrayUiState.value.solarArrayInFocus)
    }

    private fun getAllSolarArrays() {
        viewModelScope.launch {
            val allArrays = _sharedRepository.getSolarArrays()
            _solarArrayUiState.update { currentState ->
                currentState.copy(
                    arrays = allArrays,
                    solarArrayInFocus = if (allArrays.isEmpty()) null else allArrays[0]
                )
            }
        }
    }

    private fun getObservationsFromRepo(solarArray: SolarArray?) {
        viewModelScope.launch {
            if (solarArray == null) {
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Ingen solpaneler lagret"
                    )
                }
                return@launch
            }

            fetchedData =  _repository.getObservationData(solarArray.coordinates)

            val monthlyTemps = fetchedData.monthlyTemps
            val monthlySnow = fetchedData.monthlySnow
            val monthlyCloud = fetchedData.monthlyCloud
            val monthlySolarIrradiance = fetchedData.monthlyRadiation

            _graphDataUiState.update { currentState ->
                currentState.copy(
                    loadingState = "Beregner..."
                )
            }

            Log.i("test", "starting calculation")
            val electricityProduction: Map<String, Double> = calculateElectrisityProduction(
                monthlyTemps = monthlyTemps,
                monthlyCloud = monthlyCloud,
                monthlySnow = monthlySnow,
                monthlyRadiance = monthlySolarIrradiance,
                solarArray = solarArray
            )

            _graphDataUiState.update { currentState ->
                currentState.copy(
                    electricityProductionData = mapOf("Strøm Produksjon" to electricityProduction.values.toList())
                )
            }
        }
    }

    // calculating average solar irradiance using PVGIS
    private fun getSolarIrradiance() {
        viewModelScope.launch {
            val monthlyRadiance = _pvgisRepo.getMonthlySolarRadiation(59.9423, 10.72)
            Log.i("testSolar", monthlyRadiance.toString())
        }
    }
}



