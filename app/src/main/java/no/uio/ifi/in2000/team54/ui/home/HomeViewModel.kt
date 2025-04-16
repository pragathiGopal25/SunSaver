package no.uio.ifi.in2000.team54.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.util.calculateElectricityProduction
import java.util.Arrays.mismatch

data class GraphDataUiState(
    // only for ElectricityGraph
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

    // private lateinit var fetchedData: FrostRepository.FetchAllData

    private var _allData = MutableLiveData<Map<String, Double>>()
    val allData: LiveData<Map<String, Double>> get() = _allData


    private val _graphDataUiState = MutableStateFlow(GraphDataUiState())

    val solarArrays: StateFlow<List<SolarArray>> =
        _sharedRepository.solarArrays // save to SolarArraysUiState?
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

    // ny branch
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
                        loadingState = "Ingen solpaneler er lagret"
                    )
                }
                return@launch
            }

            _graphDataUiState.update { currentState ->
                currentState.copy(
                    loadingState = "Henter data... dette kan ta litt tid"
                )
            }
            try {

                val monthlyTemps = _repository.getTempData(solarArray.coordinates)
                val monthlySnow = _repository.getSnowData(solarArray.coordinates)
                val monthlyCloud = _repository.getCloudData(solarArray.coordinates)
                val monthlySolarIrradiance = _repository.getRadiationData(solarArray.coordinates)

                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Beregner estimert strømforbruk..."
                    )
                }

                //Don't calculate the same data twice
                if (!solarArrayLoadedData.containsKey(solarArray)) {
                    Log.i("test", "starting calculation")
                    val electricityProduction: Map<String, Double> = calculateElectricityProduction(
                        monthlyTemps = monthlyTemps.monthlyTemps,
                        monthlyCloud = monthlyCloud.monthlyCloud,
                        monthlySnow = monthlySnow.monthlySnow,
                        monthlyRadiance = monthlySolarIrradiance.monthlyRadiation,
                        solarArray = solarArray
                    )
                    solarArrayLoadedData[solarArray] = electricityProduction
                }

                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        electricityProductionData = mapOf("Strømproduksjon" to solarArrayLoadedData[solarArray]!!.values.toList()),
                    )
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

    // asynkronisert kombinering av data
    suspend fun fetchedData(coordinates: Coordinates) {

        // Start alle kallene parallelt
        coroutineScope {
            try {
                val asyncTemp = async { _repository.getTempData(coordinates) }
                val asyncCloud = async { _repository.getCloudData(coordinates) }
                val asyncSnow = async { _repository.getSnowData(coordinates) }
                val asyncRadiation = async { _repository.getRadiationData(coordinates) }

                val tempData = asyncTemp.await()
                val cloudData = asyncCloud.await()
                val snowData = asyncSnow.await()
                val radiationData = asyncRadiation.await()

                val temporaryMap = mutableMapOf<String, Double>()

                temporaryMap.putAll(tempData.monthlyTemps)
                temporaryMap.putAll(cloudData.monthlyCloud)
                temporaryMap.putAll(snowData.monthlySnow)
                temporaryMap.putAll(radiationData.monthlyRadiation)

                _allData.value = temporaryMap

            } catch (e: Exception) {
                e.printStackTrace()
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
                _priceUiState.update { currentState ->
                    currentState.copy(
                        loading = true
                    )
                }

                if (!calculated) {
                   // fetchedData = _repository.getObservationData(solarArray.coordinates)

                    val monthlyTemps = _repository.getTempData(solarArray.coordinates)
                    val monthlySnow = _repository.getSnowData(solarArray.coordinates)
                    val monthlyCloud = _repository.getCloudData(solarArray.coordinates)
                    val monthlySolarIrradiance = _repository.getRadiationData(solarArray.coordinates)

                    val electricityProduction: Map<String, Double> = calculateElectricityProduction(
                        monthlyTemps = monthlyTemps.monthlyTemps,
                        monthlyCloud = monthlyCloud.monthlyCloud,
                        monthlySnow = monthlySnow.monthlySnow,
                        monthlyRadiance = monthlySolarIrradiance.monthlyRadiation,
                        solarArray = solarArray
                    )
                    solarArrayLoadedData[solarArray] = electricityProduction
                }


                if (!priceMap.containsKey(days)) {
                    priceMap[days] = priceData.getPriceData(
                        days,
                        "NO1",
                        solarArrayLoadedData[solarArray]!![priceData.getMonth()]!!
                    )
                    realPriceMap[days] = priceMap[days]!![1]
                    solarPriceMap[days] = priceMap[days]!![0]
                }
                seePrices(days)
            } catch (ex: Exception) {
                _priceUiState.update { currentState ->
                    currentState.copy(
                        error = true
                    )
                }
            } finally {
                val day = Scope.entries.filter { scopeToDays[it] == days }[0]
                _priceUiState.update { currentState ->
                    currentState.copy(
                        loading = false,
                        scope = day
                    )
                }
            }
        }
    }

    private fun seePrices(days: Int) {
        viewModelScope.launch {
            try {
                _priceUiState.update { currentState ->
                    currentState.copy(
                        loading = true
                    )
                }

                val realPrice = realPriceMap[days]
                val solarPrice = solarPriceMap[days]

                if (realPrice != null) {
                    if (solarPrice != null) {
                        _priceUiState.update { currentState ->
                            currentState.copy(
                                realPrice = (Math.round(realPrice * 10) / 10.0),
                                solarPrice = (Math.round(solarPrice * 10) / 10.0),
                                saved = Math.round((realPrice - solarPrice) * 10) / 10.0,
                            )
                        }
                    }
                }
            } catch (ex: Exception) {
                _priceUiState.update { currentState ->
                    currentState.copy(
                        error = true
                    )
                }
            } finally {
                val day = Scope.entries.filter { scopeToDays[it] == days }[0]
                _priceUiState.update { currentState ->
                    currentState.copy(
                        scope = day,
                        loading = false
                    )
                }
            }
        }
    }

    fun changeTimeScope(scope: Scope) {
        viewModelScope.launch {
            try {
                _priceUiState.update { currentState ->
                    currentState.copy(
                        loading = true
                    )
                }
                seePrices(scopeToDays[scope]!!)
            } catch (ex: Exception) {
                _priceUiState.update { currentState ->
                    currentState.copy(
                        error = true
                    )
                }
            } finally {
                _priceUiState.update { currentState ->
                    currentState.copy(
                        loading = false
                    )
                }
            }
        }
    }

    data class PriceUiState(
        val realPrice: Double,
        val solarPrice: Double,
        val saved: Double,
        val loading: Boolean,
        val scope: Scope,
        val error: Boolean = false
    )

    enum class Scope {
        DAY, MONTH, YEAR
    }
}