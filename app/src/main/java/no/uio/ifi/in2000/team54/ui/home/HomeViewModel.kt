package no.uio.ifi.in2000.team54.ui.home

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
import no.uio.ifi.in2000.team54.enums.Elements
import no.uio.ifi.in2000.team54.util.calculateMonthlyElectricityProduction
import kotlin.math.round

data class HomeUiState(
    val solarArrays: StateFlow<List<SolarArray>>,
    val selectedSolarArray: SolarArray?,
    val priceData: PriceData,
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val timeScope: TimeScope = TimeScope.DAY,
    val loadingState: String = "",
    val timeUntilRecoup: Double = 0.0
)

data class LoadingState(
    val loadingMessage: String = "Ingen solanlegg er opprettet",
)

data class PriceData(
    val realPrice: Double,
    val solarPrice: Double,
    val saved: Double = round(realPrice * 10.0 - solarPrice * 10.0) / 10.0
)

data class WeatherData(
    var temp: Map<String, Double> = emptyMap<String, Double>(),
    var cloud: Map<String, Double> = emptyMap<String, Double>(),
    var snow: Map<String, Double> = emptyMap<String, Double>(),
    var irradiance: Map<String, Double> = emptyMap<String, Double>(),
    var sunhours: Map<String, Double> = emptyMap<String, Double>()
)

enum class TimeScope {
    DAY, MONTH, YEAR
}

class HomeViewModel : ViewModel() {
    private val _repository = FrostRepository()
    private val _sharedRepository = RepositoryProvider.sharedRepository

    private val weatherData = WeatherData()

    private val _graphLoadingState = MutableStateFlow(LoadingState())
    val graphLoadingState = _graphLoadingState.asStateFlow()
    private val _priceLoadingState = MutableStateFlow(LoadingState())
    val priceLoadingState = _priceLoadingState.asStateFlow()

    val solarArrays: StateFlow<List<SolarArray>> =
        _sharedRepository.solarArrays // save to SolarArraysUiState?


    private val _homeUiState = MutableStateFlow(
        HomeUiState(
            solarArrays = solarArrays,
            selectedSolarArray = null,
            priceData = PriceData(0.0, 0.0)
        )
    )

    val homeUiState = _homeUiState.asStateFlow()

    private val electricityPriceRepository =
        ElectricityPriceRepository(ElectricityPriceDatasource())

    //Når vi oppdaterer selectedSolarArray så kan data hentes fra denne mappen
    //Hvis det ikke ligger her så skal det legges inn
    private val electricityProductionMap: MutableMap<SolarArray, List<Double>> = mutableMapOf()
    private val electricityPriceMap: MutableMap<SolarArray, MutableMap<TimeScope, PriceData>> =
        mutableMapOf()
    private val totalPriceMap: MutableMap<SolarArray, Double> = mutableMapOf()

    private val timeScopeToDays =
        mapOf(TimeScope.DAY to 1, TimeScope.MONTH to 30, TimeScope.YEAR to 365)

    init {
        viewModelScope.launch {
            solarArrays.filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collect { solarArrays ->
                    val firstSolarArray = solarArrays.firstOrNull()

                    if (firstSolarArray != null) {
                        _homeUiState.update { currentState ->
                            currentState.copy(
                                selectedSolarArray = firstSolarArray
                            )
                        }

                        getWeatherData(firstSolarArray.coordinates)

                        useWeatherData(firstSolarArray)

                        selectSolarArray(firstSolarArray)
                    }
                }
        }
    }

    // asynkronisert kombinering av data
    suspend fun getWeatherData(coordinates: Coordinates) {

        coroutineScope { // Starter alle kallene parallellt
            try {
                _graphLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Henter data om været ..."
                    )
                }
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Henter data om været ..."
                    )
                }
                val asyncTemp = async { _repository.getData(coordinates, Elements.TEMP) }
                val asyncCloud = async { _repository.getData(coordinates, Elements.CLOUD) }
                val asyncSnow = async { _repository.getData(coordinates, Elements.SNOW) }
                val asyncIrradiance = async { _repository.getData(coordinates, Elements.IRRIDANCE) }
                val asyncSunhours = async { _repository.getData(coordinates, Elements.SUNHOURS) }

                val tempData = asyncTemp.await()
                val cloudData = asyncCloud.await()
                val snowData = asyncSnow.await()
                val irradianceData = asyncIrradiance.await()
                val sunhoursData = asyncSunhours.await()

                weatherData.temp = tempData
                weatherData.cloud = cloudData
                weatherData.snow = snowData
                weatherData.irradiance = irradianceData
                weatherData.sunhours = sunhoursData

            } catch (e: Exception) {
                _graphLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Klarte ikke å hente data om været"
                    )
                }
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Klarte ikke å hente data om været"
                    )
                }
            } finally {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = ""
                    )
                }
            }
        }
    }

    private fun useWeatherData(solarArray: SolarArray?) {

        viewModelScope.launch {

            if (solarArray == null) { // safety
                _graphLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Ingen solanlegg er opprettet"
                    )
                }
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Ingen solanlegg er opprettet"
                    )
                }
                return@launch
            }

            try {

                if (!electricityProductionMap.containsKey(solarArray)) {
                    val electricityProduction: Map<String, Double> =
                        calculateMonthlyElectricityProduction(
                            monthlyTemperatures = weatherData.temp,
                            monthlyCloud = weatherData.cloud,
                            monthlySnow = weatherData.snow,
                            monthlyRadiance = weatherData.irradiance,
                            monthlySunhours = weatherData.sunhours,
                            solarArray = solarArray
                        )
                    electricityProductionMap[solarArray] = electricityProduction.values.toList()
                }

                _homeUiState.update { currentState ->
                    currentState.copy(
                        //Hvorfor trenger vi at dette er en map med "strømproduksjon som key?
                        electricityProductionData = mapOf("Strømproduksjon" to electricityProductionMap[solarArray]!!),
                    )
                }
            } catch (e: Exception) {
                _graphLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Noe gikk galt med innhenting av data."
                    )
                }
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Noe gikk galt med innhenting av data."
                    )
                }
            } finally {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = ""
                    )
                }
            }
        }
    }

    fun selectSolarArray(solarArray: SolarArray) {
        viewModelScope.launch {
            if (homeUiState.value.solarArrays.value.isEmpty()) return@launch
            try {
                useWeatherData(solarArray)
                loadElectricityPrices(solarArray)
                _homeUiState.update { currentState ->
                    currentState.copy(
                        selectedSolarArray = solarArray,
                    )
                }
            } catch (ex: Exception) {
                _homeUiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Klarte ikke å velge solcelleanlegg"
                    )
                }
            }
        }
    }

    private fun loadElectricityPrices(solarArray: SolarArray) {
        viewModelScope.launch {
            try {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Laster inn strømpriser..."
                    )
                }
                if (!electricityPriceMap.containsKey(solarArray)) {
                    timeScopeToDays.forEach { (scope, days) ->
                        val priceDataTuple = electricityPriceRepository.getPriceData(
                            days,
                            electricityPriceRepository.getPriceArea(solarArray),
                            electricityProductionMap[solarArray]!![electricityPriceRepository.getMonth()],
                            solarArray.powerConsumption
                        )
                        val priceData = PriceData(
                            realPrice = round(priceDataTuple[1] * 10.0) / 10.0,
                            solarPrice = round(priceDataTuple[0] * 10.0) / 10.0,
                        )
                        electricityPriceMap.computeIfAbsent(
                            solarArray,
                            { mutableMapOf() })[scope] = priceData
                    }
                }
                seePrices(_homeUiState.value.timeScope, solarArray)
                calculateRecoup(solarArray)
            } catch (ex: Exception) {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Klarte ikke å laste inn strømpriser"
                    )
                }
            } finally {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = ""
                    )
                }
            }
        }
    }

    private fun seePrices(timeScope: TimeScope, solarArray: SolarArray) {
        viewModelScope.launch {
            try {
                if (!electricityPriceMap.containsKey(solarArray)) loadElectricityPrices(solarArray)
                _homeUiState.update { currentState ->
                    currentState.copy(
                        priceData = electricityPriceMap[_homeUiState.value.selectedSolarArray]!![timeScope]!!
                    )
                }
            } catch (ex: Exception) {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Klarte ikke å laste inn strømpriser"
                    )
                }
            }
        }
    }

    fun changeTimeScope(timeScope: TimeScope) {
        viewModelScope.launch {
            try {
                _homeUiState.update { currentState ->
                    currentState.copy(
                        timeScope = timeScope
                    )
                }
                seePrices(timeScope, _homeUiState.value.selectedSolarArray!!)
            } catch (ex: Exception) {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Kunne ikke endre tidsintervall."
                    )
                }
            }
        }
    }


    fun calculateRecoup(solarArray: SolarArray) {
        val totalPrice = solarArray.getTotalPrice()
        _homeUiState.update { currentState ->
            currentState.copy(
                timeUntilRecoup = round((totalPrice / electricityPriceMap[currentState.selectedSolarArray]!![TimeScope.YEAR]!!.saved) * 100.0) / 100.0
            )
        }
    }
}
