package no.uio.ifi.in2000.team54.ui.home

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
import no.uio.ifi.in2000.team54.enums.Elements
import no.uio.ifi.in2000.team54.util.calculateMonthlyElectricityProduction


// Ha selectedsolararray som key i en map som er koblet til data
data class HomeUiState(
    val solarArrays: StateFlow<List<SolarArray>>, //fungerer som tidligere
    val selectedSolarArray: SolarArray?,
    val priceData: PriceData, //oppdater data etter selected solar array
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val scope: Scope = Scope.DAY, //kan renames til mer spesifikt
    val loadingState: String = ""
)

data class LoadingState(
    val loadingMessage: String = "Ingen solanlegg er opprettet",
)

data class PriceData(
    val realPrice: Double,
    val solarPrice: Double,
    val saved: Double = Math.round(realPrice * 10.0 - solarPrice * 10.0) / 10.0
)

data class WeatherData(
    var temp: Map<String, Double> = emptyMap(),
    var cloud: Map<String, Double> = emptyMap(),
    var snow: Map<String, Double> = emptyMap(),
    var irradiance: Map<String, Double> = emptyMap()
)

enum class Scope {
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


    private val _uiState = MutableStateFlow(
        HomeUiState(
            solarArrays = solarArrays,
            selectedSolarArray = null,
            priceData = PriceData(0.0, 0.0) //data = dataMap.get(selectedSolarArray)
        )
    )

    val uiState = _uiState.asStateFlow()

    private val electricityPriceRepository =
        ElectricityPriceRepository(ElectricityPriceDatasource())

    //Når vi oppdaterer selectedSolarArray så kan data hentes fra denne mappen
    //Hvis det ikke ligger her så skal det legges inn
    private val dataMap: MutableMap<SolarArray, List<Double>> = mutableMapOf()
    private val priceDataMap: MutableMap<SolarArray, MutableMap<Scope, PriceData>> =
        mutableMapOf()
    private val scopeToDays = mapOf(Scope.DAY to 1, Scope.MONTH to 30, Scope.YEAR to 365)

    init {
        viewModelScope.launch {
            solarArrays.filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collect { solarArrays ->
                    val firstSolarArray = solarArrays.firstOrNull()

                    if (firstSolarArray != null) {
                        _uiState.update { currentState ->
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

                val tempData = asyncTemp.await()
                val cloudData = asyncCloud.await()
                val snowData = asyncSnow.await()
                val irradianceData = asyncIrradiance.await()

                weatherData.temp = tempData
                weatherData.cloud = cloudData
                weatherData.snow = snowData
                weatherData.irradiance = irradianceData

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
                if (!dataMap.containsKey(solarArray)) {
                    val electricityProduction: Map<String, Double> = //map: <måned,strømproduksjon>
                        calculateMonthlyElectricityProduction(
                            monthlyTemperatures = weatherData.temp,
                            monthlyCloud = weatherData.cloud,
                            monthlySnow = weatherData.snow,
                            monthlyRadiance = weatherData.irradiance,
                            solarArray = solarArray
                        )
                    dataMap[solarArray] = electricityProduction.values.toList()
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        //Hvorfor trenger vi at dette er en map med "strømproduksjon som key?
                        electricityProductionData = mapOf("Strømproduksjon" to dataMap[_uiState.value.selectedSolarArray]!!),
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
            if (uiState.value.solarArrays.value.isEmpty()) return@launch //Må velge fra liste
            try {
                useWeatherData(solarArray)
                loadElectricityPrices(solarArray)
                Log.i("selectSolarArray()", "passed loadData()")
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedSolarArray = solarArray,
                        scope = Scope.DAY,
                        priceData = priceDataMap[solarArray]!![_uiState.value.scope]!!
                    )
                }
//                seePrices(_uiState.value.scope, solarArray)

            } catch (ex: Exception) {
                _uiState.update { currentState ->
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
                if (!priceDataMap.containsKey(solarArray)) {
                    scopeToDays.forEach { (scope, count) ->
                        val priceDataTuple = electricityPriceRepository.getPriceData(
                            count,
                            "NO1",
                            dataMap[solarArray]!![electricityPriceRepository.getMonth()]
                        )
                        val priceData = PriceData(
                            realPrice = Math.round(priceDataTuple[1] * 10.0) / 10.0,
                            solarPrice = Math.round(priceDataTuple[0] * 10.0) / 10.0,
                        )
                        priceDataMap.computeIfAbsent(
                            solarArray,
                            { mutableMapOf() })[scope] = priceData
                    }
                }
                seePrices(_uiState.value.scope, solarArray)
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

    private fun seePrices(scope: Scope, solarArray: SolarArray) {
        viewModelScope.launch {
            try {
                if (!priceDataMap.containsKey(solarArray)) loadElectricityPrices(solarArray)
                _uiState.update { currentState ->
                    currentState.copy(
                        priceData = priceDataMap[_uiState.value.selectedSolarArray]!![scope]!!
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

    fun changeTimeScope(scope: Scope) {
        viewModelScope.launch {
            try {
                _uiState.update { currentState ->
                    currentState.copy(
                        scope = scope
                    )
                }
                seePrices(scope, _uiState.value.selectedSolarArray!!)
            } catch (ex: Exception) {
                _priceLoadingState.update { currentState ->
                    currentState.copy(
                        loadingMessage = "Feilet på å endre tidsintervall"
                    )
                }
            }
        }
    }
}