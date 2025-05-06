package no.uio.ifi.in2000.team54.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.electricity.ElectricityPriceDatasource
import no.uio.ifi.in2000.team54.data.electricity.ElectricityPriceRepository
import no.uio.ifi.in2000.team54.data.frost.FrostRepository
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.Elements
import no.uio.ifi.in2000.team54.ui.network.NetworkObserver
import no.uio.ifi.in2000.team54.util.calculateMonthlyElectricityProduction
import kotlin.math.round

data class HomeUiState(
    val solarArrays: List<SolarArray> = emptyList(),
    val selectedSolarArray: SolarArray? = null,
    val priceData: PriceData = PriceData(0.0, 0.0),
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val timeScope: TimeScope = TimeScope.DAY,
    val timeUntilRecoup: Double = 0.0
)

data class LoadingState(
    val isLoading: Boolean = false,
    // empty means the data is already processed
    val statusMessage: String = "Ingen solanlegg er opprettet",
)

data class PriceData(
    val realPrice: Double,
    val solarPrice: Double,
    val saved: Double = round(realPrice * 100.0 - solarPrice * 100.0) / 100.0
)

data class WeatherData(
    var temp: Map<String, Double> = emptyMap(),
    var cloud: Map<String, Double> = emptyMap(),
    var snow: Map<String, Double> = emptyMap(),
    var irradiance: Map<String, Double> = emptyMap(),
    var sunhours: Map<String, Double> = emptyMap()
)

enum class TimeScope {
    DAY, MONTH, YEAR
}

class HomeViewModel(
    private val networkObserver: NetworkObserver
) : ViewModel() {
    private val _repository = FrostRepository()
    private val _sunSaverRepository = RepositoryProvider.sunSaverRepository
    private val electricityPriceRepository =
        ElectricityPriceRepository(ElectricityPriceDatasource())

    // loading states
    private val _graphLoadingState = MutableStateFlow(LoadingState())
    val graphLoadingState = _graphLoadingState.asStateFlow()
    private val _priceLoadingState = MutableStateFlow(LoadingState())
    val priceLoadingState = _priceLoadingState.asStateFlow()

    // home ui state
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    // snackbar
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    // saved data
    private val electricityProductionMap: MutableMap<SolarArray, List<Double>> = mutableMapOf()
    private val electricityPriceMap: MutableMap<SolarArray, MutableMap<TimeScope, PriceData>> =
        mutableMapOf()
    private val weatherDataMap: MutableMap<SolarArray, WeatherData> = mutableMapOf()
    private val priceDataMap: MutableMap<SolarArray, MutableMap<TimeScope, Double>> = mutableMapOf()

    private val timeScopeToDays =
        mapOf(TimeScope.DAY to 1, TimeScope.MONTH to 30, TimeScope.YEAR to 365)

    private val _isOnline = MutableStateFlow(true)
    val isOnline = _isOnline.asStateFlow()

    init {
        _isOnline.value = networkObserver.isNetworkAvailable()
        observeNetwork()

        viewModelScope.launch {
            _sunSaverRepository.getAllSolarArrays()
                .collect { solarArraysList ->
                    if (solarArraysList.isEmpty()) {
                        _homeUiState.value = HomeUiState()
                        _graphLoadingState.value = LoadingState()
                        _priceLoadingState.value = LoadingState()
                        return@collect
                    }
                    var selectedSolarArray = solarArraysList.firstOrNull()
                    val savedList = _homeUiState.value.solarArrays

                    // added new
                    if (solarArraysList.size > savedList.size && savedList.isNotEmpty()) {
                        selectedSolarArray = solarArraysList.lastOrNull()
                    }

                    var isUpdated = false
                    if (solarArraysList.size == savedList.size) { // updated
                        selectedSolarArray = findUpdated(
                            oldList = savedList,
                            newList = solarArraysList
                        )
                        isUpdated = true
                    }

                    _homeUiState.update { currentState ->
                        currentState.copy(
                            solarArrays = solarArraysList,
                        )
                    }

                    if (selectedSolarArray != null) {
                        selectSolarArray(selectedSolarArray, isUpdated)
                    }
                }
        }
    }

    fun selectSolarArray(solarArray: SolarArray, isUpdated: Boolean = false) {
        viewModelScope.launch {
            if (_homeUiState.value.solarArrays.isEmpty()) return@launch
            if (_graphLoadingState.value.isLoading && _priceLoadingState.value.isLoading) {
                _snackbarMessage.emit("Vent til data laster")
                return@launch
            }
            try {
                _homeUiState.update { it.copy(selectedSolarArray = solarArray) }

                // start loading
                _graphLoadingState.update { it.copy(isLoading = true) }
                _priceLoadingState.update { it.copy(isLoading = true) }

                val priceJob = launch {
                    // get electricity prices if the data is changed
                    if (!priceDataMap.containsKey(solarArray) || isUpdated) {
                        getPriceData(solarArray)
                    } else {
                        // in case the previous selected was failing, clear the error message
                        _priceLoadingState.update { it.copy(statusMessage = "") }
                    }
                }

                // get frost data if the data is changed //
                if (!weatherDataMap.containsKey(solarArray) || isUpdated) {
                    getWeatherData(solarArray)
                }

                // recalculate if new data or data is changed
                if (!electricityProductionMap.containsKey(solarArray) || isUpdated) {
                    useWeatherData(solarArray)
                } else {
                    // in case the previous selected was failing, clear the error message
                    _graphLoadingState.update { it.copy( statusMessage = "") }
                }

                _homeUiState.update { currentState ->
                    currentState.copy(
                        electricityProductionData = mapOf(
                            "Strømproduksjon" to electricityProductionMap[solarArray]!!
                        )
                    )
                }

                // ready to show the graph (or the error message)
                _graphLoadingState.update { it.copy(isLoading = false) }

                priceJob.join()
                loadElectricityPrices(solarArray)

                // ready to show the prices (or error message)
                _priceLoadingState.update { it.copy(isLoading = false) }

            } catch (_: Exception) {

            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkObserver.isConnected.collectLatest { connected ->
                _isOnline.value = connected
            }
        }
    }

    // asynkronisert kombinering av data
    private suspend fun getWeatherData(solarArray: SolarArray) {
        coroutineScope { // Starter alle kallene parallellt
            try {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        isLoading = true
                    )
                }

                val coordinates = solarArray.coordinates
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

                val weatherData = WeatherData()
                weatherData.temp = tempData
                weatherData.cloud = cloudData
                weatherData.snow = snowData
                weatherData.irradiance = irradianceData
                weatherData.sunhours = sunhoursData

                weatherDataMap[solarArray] = weatherData

            } catch (e: Exception) {
                _graphLoadingState.update { currentState ->
                    currentState.copy(
                        statusMessage = "Klarte ikke å hente data om været",
                        isLoading = false
                    )
                }
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        statusMessage = "Klarte ikke å hente data om været",
                        isLoading = false
                    )
                }
            }
        }
    }

    // get price data in a way that can be done async
    private suspend fun getPriceData(solarArray: SolarArray) {
        try {
            val area = electricityPriceRepository.getPriceArea(solarArray)
            timeScopeToDays.forEach { (scope, days) ->
                val avgDailyElectricityPrice =
                    electricityPriceRepository.getPriceDataInterval(days, area).average()

                priceDataMap.computeIfAbsent(
                    solarArray
                ) { mutableMapOf() }[scope] = avgDailyElectricityPrice
            }

            _priceLoadingState.update { currentState ->
                currentState.copy(
                    statusMessage = ""
                )
            }
        } catch (e: Exception) {
            _priceLoadingState.update {
                it.copy(
                    statusMessage = "Kunne ikke hente strømpriser",
                    isLoading = false
                )
            }
        }
    }

    private fun useWeatherData(solarArray: SolarArray) {
        try {
            val weatherData = weatherDataMap[solarArray]!!
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

            _graphLoadingState.update { currentState ->
                currentState.copy(
                    statusMessage = ""
                )
            }
        } catch (e: Exception) {
            _graphLoadingState.update { currentState ->
                currentState.copy(
                    statusMessage = "Noe gikk galt med innhenting av data.",
                    isLoading = false
                )
            }
            _priceLoadingState.update { currentState ->
                currentState.copy(
                    statusMessage = "Noe gikk galt med innhenting av data.",
                    isLoading = false
                )
            }
        }
    }

    fun removeSolarArray(solarArray: SolarArray) {
        viewModelScope.launch {
            if (_graphLoadingState.value.isLoading && _priceLoadingState.value.isLoading) {
                _snackbarMessage.emit("Vent til data laster")
                return@launch
            }
            try {
                _sunSaverRepository.deleteSolarArray(solarArray)
            } catch (ex: Exception) {
                _snackbarMessage.emit("Klarte ikke å slette solcelleanlegg")
            }
        }
    }


    private fun loadElectricityPrices(solarArray: SolarArray) {
        try {
            if (!electricityPriceMap.containsKey(solarArray)) {
                timeScopeToDays.forEach { (scope, days) ->
                    val powerUsage =
                        if (scope == TimeScope.YEAR) electricityProductionMap[solarArray]!!.average()
                        else electricityProductionMap[solarArray]!![electricityPriceRepository.getMonth()]

                    val priceDataTuple = electricityPriceRepository.getPriceData(
                        days,
                        powerUsage,
                        solarArray.powerConsumption,
                        priceDataMap[solarArray]!![scope]!!
                    )

                    val priceData = PriceData(
                        realPrice = round(priceDataTuple[1] * 100.0) / 100.0,
                        solarPrice = round(priceDataTuple[0] * 100.0) / 100.0,
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
                    statusMessage = "Klarte ikke å laste inn strømpriser",
                    isLoading = false
                )
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
                        statusMessage = "Klarte ikke å laste inn strømpriser",
                        isLoading = false
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
                loadElectricityPrices(_homeUiState.value.selectedSolarArray!!)
            } catch (ex: Exception) {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        statusMessage = "Kunne ikke endre tidsintervall.",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun calculateRecoup(solarArray: SolarArray) {
        val totalPrice = solarArray.getTotalPrice()
        _homeUiState.update { currentState ->
            currentState.copy(
                timeUntilRecoup = round((totalPrice / electricityPriceMap[currentState.selectedSolarArray]!![TimeScope.YEAR]!!.saved) * 100.0) / 100.0
            )
        }
    }

    private fun findUpdated(
        oldList: List<SolarArray>,
        newList: List<SolarArray>
    ): SolarArray? {
        val oldMap = oldList.associateBy { it.id }

        return newList.find { newItem ->
            val oldItem = oldMap[newItem.id]
            // New if oldItem is null, updated if it exists but is not equal
            oldItem != null && oldItem != newItem
        }
    }
}
