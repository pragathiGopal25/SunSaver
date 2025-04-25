package no.uio.ifi.in2000.team54.ui.home

import android.R.attr.data
import android.util.Log
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
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.util.calculateMonthlyElectricityProduction
import no.uio.ifi.in2000.team54.enums.Elements

data class GraphDataUiState(
    // only for ElectricityGraph
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val loadingState: String = "Ingen solanlegg er opprettet",
)

data class WeatherData(
    var temp: Map<String, Double> = emptyMap<String, Double>(),
    var cloud: Map<String, Double> = emptyMap<String, Double>(),
    var snow: Map<String, Double> = emptyMap<String, Double>(),
    var irradiance: Map<String, Double> = emptyMap<String, Double>()
)


class HomeViewModel : ViewModel() {
    private val _repository = FrostRepository()
    private val _sharedRepository = RepositoryProvider.sharedRepository

    private val weatherData= WeatherData()

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

                       getWeatherData(firstSolarArray.coordinates)

                        useWeatherData(firstSolarArray)

                        Scope.entries.forEach {
                            loadData(scopeToDays[it]!!, firstSolarArray)
                        }
                    }
                }
        }
    }

    // asynkronisert kombinering av data
    suspend fun getWeatherData(coordinates: Coordinates) {

        coroutineScope { // Starter alle kallene parallellt
            try {
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Beregner estimert strømforbruk ..."
                    )
                }
                val asyncTemp = async { _repository.getData(coordinates, Elements.TEMP) }
                val asyncCloud = async { _repository.getData(coordinates, Elements.CLOUD) }
                val asyncSnow = async { _repository.getData(coordinates, Elements.SNOW) }
                val asyncIrradiance = async { _repository.getData(coordinates,Elements.IRRIDANCE) }

                val tempData = asyncTemp.await()
                val cloudData = asyncCloud.await()
                val snowData = asyncSnow.await()
                val irradianceData = asyncIrradiance.await()

                weatherData.temp = tempData
                weatherData.cloud = cloudData
                weatherData.snow = snowData
                weatherData.irradiance = irradianceData

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun useWeatherData(solarArray: SolarArray?) {

        viewModelScope.launch {

            if (solarArray == null) { // safety
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Ingen solanlegg er oppretter"
                    )
                }
                return@launch
            }

            try {

                //Don't calculate the same data twice
                // TODO: fiks under implementeasjon av valg av solararray in focus

                if (!solarArrayLoadedData.containsKey(solarArray)) {
                    val electricityProduction: Map<String, Double> = calculateMonthlyElectricityProduction(
                        monthlyTemperatures = weatherData.temp,
                        monthlyCloud = weatherData.cloud,
                        monthlySnow = weatherData.snow,
                        monthlyRadiance = weatherData.irradiance,
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
                Log.e("HomeViewModel", "Feil ved innlasting av værdata", e)
                _graphDataUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Noe gikk galt med innhenting av data."
                    )
                }
            }
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