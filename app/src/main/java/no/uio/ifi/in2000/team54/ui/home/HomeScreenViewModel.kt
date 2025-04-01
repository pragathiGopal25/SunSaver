package no.uio.ifi.in2000.team54.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.frost.FrostRepository
import no.uio.ifi.in2000.team54.data.pvgis.PVGISRepository
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.util.calculateElectrisityProduction

data class GraphDataUiState( // only for ElectrisityGraph
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val loadingState: String = "Ingen solceller lagret",
)

data class SolarArrayUiState( // i guess we fetch separately for different addresses
    val arrays: List<SolarArray> = emptyList(), //
    val solarArrayInFocus: SolarArray? = null
)

class HomeScreenViewModel: ViewModel() {
    private val _repository = FrostRepository()
    private val _pvgisRepo = PVGISRepository() // probably will delete later
    private val _sharedRepository = RepositoryProvider.sharedRepository

    private lateinit var fetchedData: FrostRepository.FetchAllData

    private val _graphDataUiState = MutableStateFlow(GraphDataUiState())

    val solarArrays: StateFlow<List<SolarArray>> = _sharedRepository.solarArrays // save to SolarArraysUiState?
    val graphDataUiState = _graphDataUiState.asStateFlow()

    init {
        viewModelScope.launch {
            solarArrays.filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collect { list ->
                    val firstItem2 = list.firstOrNull()
                    if (firstItem2 != null) {
                        getObservationsFromRepo(firstItem2)
                    }
                }
        }
    }

    private fun getObservationsFromRepo(solarArray: SolarArray?) {
        viewModelScope.launch {
            if (solarArray == null) { // safety
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Ingen solpaneler lagret"
                    )
                }
                return@launch
            }

            _graphDataUiState.update { currentState ->
                currentState.copy(
                    loadingState = "Henter data... det kan ta tid"
                )
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

            val electricityProduction: Map<String, Double> = calculateElectrisityProduction(
                monthlyTemps = monthlyTemps,
                monthlyCloud = monthlyCloud,
                monthlySnow = monthlySnow,
                monthlyRadiance = monthlySolarIrradiance,
                solarArray = solarArray
            )

            _graphDataUiState.update { currentState ->
                currentState.copy(
                    electricityProductionData = mapOf("Strømproduksjon" to electricityProduction.values.toList()),
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



