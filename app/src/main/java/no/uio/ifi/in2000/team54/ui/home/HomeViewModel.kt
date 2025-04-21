package no.uio.ifi.in2000.team54.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class GraphDataUiState(
    // only for ElectricityGraph
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val loadingState: String = "Ingen solanlegg er opprettet",
)

data class SolarArrayUiState( // i guess we fetch separately for different addresses
    val arrays: List<SolarArray> = emptyList(), //
    val solarArrayInFocus: SolarArray? = null
)

class HomeViewModel : ViewModel() {
    private val _repository = FrostRepository()
    private val _pvgisRepo = PVGISRepository() // probably will delete later
    private val _sharedRepository = RepositoryProvider.sharedRepository

    private var _allData = MutableLiveData<Map<String, Map<String, Double>>>()

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

    init {
        viewModelScope.launch {
            solarArrays.filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collect { solarArrays ->
                    val firstSolarArray = solarArrays.firstOrNull()
                    if (firstSolarArray != null) {

                       fetchedData(firstSolarArray.coordinates)

                        getWeatherData(firstSolarArray)

                        Scope.entries.forEach {
                            loadData(scopeToDays[it]!!, firstSolarArray)
                        }
                    }
                }
        }
    }

    private fun getWeatherData(solarArray: SolarArray?) {

        viewModelScope.launch {

            val data = _allData.value

            if (solarArray == null) { // safety
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Ingen solanlegg er oppretter"
                    )
                }
                return@launch
            }

            try {

                val monthlyTemp = data?.get("Temp") ?: return@launch
                val monthlyCloud = data["Cloud"] ?: return@launch
                val monthlySnow = data["Snow"] ?: return@launch
                val monthlyRadiation = data["Radiation"] ?: return@launch

                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Beregner estimert strømforbruk..."
                    )
                }


                //Don't calculate the same data twice
                if (!solarArrayLoadedData.containsKey(solarArray)) {
                    Log.i("Test", "starting calculation")
                    val electricityProduction: Map<String, Double> = calculateElectricityProduction(
                        monthlyTemps = monthlyTemp,
                        monthlyCloud = monthlyCloud,
                        monthlySnow = monthlySnow,
                        monthlyRadiance = monthlyRadiation,
                        solarArray = solarArray
                    )
                    solarArrayLoadedData[solarArray] = electricityProduction
                }

                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        electricityProductionData = mapOf("Strømproduksjon" to solarArrayLoadedData[solarArray]!!.values.toList()),
                    )
                }

            } catch (e: Exception) {
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Noe gikk galt med innhenting av data."
                    )
                }
            }

        }
    }

    // asynkronisert kombinering av data
    suspend fun fetchedData(coordinates: Coordinates) {

        coroutineScope { // Starter alle kallene parallellt
            try {
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Henter data... dette kan ta litt tid"
                    )
                }
                val asyncTemp = async { _repository.getData(coordinates, "temp") }
                val asyncCloud = async { _repository.getData(coordinates, "cloud") }
                val asyncSnow = async { _repository.getData(coordinates, "snow") }
                val asyncRadiation = async { _repository.getData(coordinates,"radiation") }

                val tempData = asyncTemp.await()
                val cloudData = asyncCloud.await()
                val snowData = asyncSnow.await()
                val radiationData = asyncRadiation.await()

                val temporaryMap = mutableMapOf<String, Map<String, Double>>()

                temporaryMap["Temp"] = tempData
                temporaryMap["Cloud"] = cloudData
                temporaryMap["Snow"] = snowData
                temporaryMap["Radiation"] = radiationData

                _allData.value = temporaryMap
                Log.i("All_Data", _allData.value.toString())

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

                // Hvis værdata allerede er hentet, bruk dem fra _allData
                val data = _allData.value

                if (data != null && data.isNotEmpty()) { // Bruk hentede data fra _allData

                    val monthlyTemp = data["Temp"] ?: return@launch
                    val monthlyCloud = data["Cloud"] ?: return@launch
                    val monthlySnow = data["Snow"] ?: return@launch
                    val monthlyRadiation = data["Radiation"] ?: return@launch

                    val electricityProduction: Map<String, Double> = calculateElectricityProduction(
                        monthlyTemps = monthlyTemp,
                        monthlyCloud = monthlyCloud,
                        monthlySnow = monthlySnow,
                        monthlyRadiance = monthlyRadiation,
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