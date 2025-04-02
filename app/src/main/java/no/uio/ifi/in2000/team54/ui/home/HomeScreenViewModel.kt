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
import no.uio.ifi.in2000.team54.data.electricity.ElectricityPriceDatasource
import no.uio.ifi.in2000.team54.data.electricity.ElectricityPriceRepository
import no.uio.ifi.in2000.team54.data.frost.FrostRepository
import no.uio.ifi.in2000.team54.data.pvgis.PVGISRepository
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.util.calculateElectricityProduction

data class GraphDataUiState( // only for ElectricityGraph
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val loadingState: String = "Ingen solceller lagret",
)

data class SolarArrayUiState( // i guess we fetch separately for different addresses
    val arrays: List<SolarArray> = emptyList(), //
    val solarArrayInFocus: SolarArray? = null
)

class HomeScreenViewModel : ViewModel() {
    private val _repository = FrostRepository()
    private val _pvgisRepo = PVGISRepository() // probably will delete later
    private val _sharedRepository = RepositoryProvider.sharedRepository

    private lateinit var fetchedData: FrostRepository.FetchAllData

    private val _graphDataUiState = MutableStateFlow(GraphDataUiState())

    val solarArrays: StateFlow<List<SolarArray>> = _sharedRepository.solarArrays // save to SolarArraysUiState?
    val graphDataUiState = _graphDataUiState.asStateFlow()

    private val _priceUiState = MutableStateFlow(PriceUiState(0.0, 0.0, 0.0, false, Scope.DAY))
    private val priceData = ElectricityPriceRepository(ElectricityPriceDatasource())
    val priceUiState: StateFlow<PriceUiState> = _priceUiState.asStateFlow()

    private val scopeToDays = mapOf(Scope.DAY to 1, Scope.MONTH to 30, Scope.YEAR to 365)
    private val realPriceMap = mutableMapOf<Int, Double>()
    private val priceMap = mutableMapOf<Int, List<Double>>()
    private val solarPriceMap = mutableMapOf<Int, Double>()
    private var solarArrayLoadedData = mutableMapOf<SolarArray, Map<String, Double>>()
    private val calculated = false


    init {
        viewModelScope.launch {
            solarArrays.filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collect { solarArrays ->
                    val firstSolarArray = solarArrays.firstOrNull()
                    if (firstSolarArray != null) {
                        getObservationsFromRepo(firstSolarArray)
                        Scope.entries.forEach {
                            loadData(scopeToDays[it]!!, firstSolarArray)
                        }
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
            try {
                fetchedData = _repository.getObservationData(solarArray.coordinates)

                val monthlyTemps = fetchedData.monthlyTemps
                val monthlySnow = fetchedData.monthlySnow
                val monthlyCloud = fetchedData.monthlyCloud
                val monthlySolarIrradiance = fetchedData.monthlyRadiation

                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Beregner..."
                    )
                }

                //Don't calculate the same data twice
                if (!solarArrayLoadedData.containsKey(solarArray)) {
                    Log.i("test", "starting calculation")
                    val electricityProduction: Map<String, Double> = calculateElectricityProduction(
                        monthlyTemps = monthlyTemps,
                        monthlyCloud = monthlyCloud,
                        monthlySnow = monthlySnow,
                        monthlyRadiance = monthlySolarIrradiance,
                        solarArray = solarArray
                    )
                    solarArrayLoadedData[solarArray] = electricityProduction

                    _graphDataUiState.update { currentState ->
                        currentState.copy(
                            electricityProductionData = mapOf("Strømproduksjon" to electricityProduction.values.toList()),
                        )
                    }
                }
            } catch (ex: Exception) {
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Noe gikk galt."
                    )
                }
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

    private fun loadData(days: Int, solarArray: SolarArray) {
        viewModelScope.launch {
            try {
                _priceUiState.value =
                    _priceUiState.value.copy(
                        loading = true
                    )

                if (!calculated) {
                    fetchedData = _repository.getObservationData(solarArray.coordinates)

                    val monthlyTemps = fetchedData.monthlyTemps
                    val monthlySnow = fetchedData.monthlySnow
                    val monthlyCloud = fetchedData.monthlyCloud
                    val monthlySolarIrradiance = fetchedData.monthlyRadiation

                    val electricityProduction: Map<String, Double> = calculateElectricityProduction(
                        monthlyTemps = monthlyTemps,
                        monthlyCloud = monthlyCloud,
                        monthlySnow = monthlySnow,
                        monthlyRadiance = monthlySolarIrradiance,
                        solarArray = solarArray
                    )
                    solarArrayLoadedData[solarArray] = electricityProduction
                }

                _priceUiState.value =
                    _priceUiState.value.copy(
                        loading = true
                    )
                if (!priceMap.containsKey(days)) {
                    priceMap[days] = priceData.getPriceData(
                        days,
                        "NO1",
                        solarArrayLoadedData[solarArray]!![priceData.getMonth()]!!
                    )
                    realPriceMap[days] = priceMap[days]!![1]
                    solarPriceMap[days] = priceMap[days]!![0]
                }
            } finally {
                seePrices(days)
                val day = Scope.entries.filter { scopeToDays[it] == days }[0]
                _priceUiState.value =
                    _priceUiState.value.copy(
                        scope = day,
                        loading = false
                    )
            }
        }
    }

    private fun seePrices(days: Int) {
        viewModelScope.launch {
            try {
                _priceUiState.value =
                    _priceUiState.value.copy(
                        loading = true
                    )

                val realPrice = realPriceMap[days]
                val solarPrice = solarPriceMap[days]

                if (realPrice != null) {
                    if (solarPrice != null) {
                        _priceUiState.value =
                            _priceUiState.value.copy(
                                realPrice = (Math.round(realPrice * 10) / 10.0),
                                solarPrice = (Math.round(solarPrice * 10) / 10.0),
                                saved = Math.round((realPrice - solarPrice) * 10) / 10.0,
                                loading = false
                            )
                    }
                }
            } finally {
                val day = Scope.entries.filter { scopeToDays[it] == days }[0]
                _priceUiState.value =
                    _priceUiState.value.copy(
                        scope = day
                    )
            }
        }
    }

    fun changeTimeScope(scope: Scope) {
        viewModelScope.launch {
            try {
                seePrices(scopeToDays[scope]!!)
            } finally {

            }
        }
    }

    data class PriceUiState(
        val realPrice: Double,
        val solarPrice: Double,
        val saved: Double,
        val loading: Boolean,
        val scope: Scope
    )

    enum class Scope {
        DAY, MONTH, YEAR
    }
}