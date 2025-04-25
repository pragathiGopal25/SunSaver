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
    val loadingState: String = "Ingen solanlegg er opprettet",
    val scope: Scope = Scope.DAY, //kan renames til mer spesifikt
)

data class PriceData(
    //map key:scope til value:resten
    val realPrice: Double = 0.0,
    val solarPrice: Double = 0.0,
    val saved: Double = 0.0,
)


data class GraphDataUiState(
    // only for ElectricityGraph
    val electricityProductionData: Map<String, List<Double>> = emptyMap(),
    val loadingState: String = "Ingen solanlegg er opprettet",
)

//behold denne klassen
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


    private val _graphDataUiState = MutableStateFlow(GraphDataUiState())

    val solarArrays: StateFlow<List<SolarArray>> =
        _sharedRepository.solarArrays // save to SolarArraysUiState?
    val graphDataUiState = _graphDataUiState.asStateFlow()


    private val _uiState = MutableStateFlow(
        HomeUiState(
            solarArrays = solarArrays,
            selectedSolarArray = null,
            priceData = PriceData() //data = dataMap.get(selectedSolarArray)
        )
    )

    val uiState = _uiState.asStateFlow()

    private val priceData = ElectricityPriceRepository(ElectricityPriceDatasource())

    //Når vi oppdaterer selectedSolarArray så kan data hentes fra denne mappen
    //Hvis det ikke ligger her så skal det legges inn
    private var dataMap: Map<SolarArray, List<Double>> = mutableMapOf()
    private val priceDataMap: Map<SolarArray, Map<Scope, PriceData>> = mutableMapOf()
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
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Henter data om været ..."
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
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Klarte ikke å hente data om været"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    private fun useWeatherData(solarArray: SolarArray?) {

        viewModelScope.launch {

            if (solarArray == null) { // safety
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Ingen solanlegg er opprettet"
                    )
                }
                return@launch
            }

            try {

                //Don't calculate the same data twice
                // TODO: fiks under implementeasjon av valg av solararray in focus

//                if (!dataMap.containsKey(solarArray)) {
                val electricityProduction: Map<String, Double> = //map: <måned,strømproduksjon>
                    calculateMonthlyElectricityProduction(
                        monthlyTemperatures = weatherData.temp,
                        monthlyCloud = weatherData.cloud,
                        monthlySnow = weatherData.snow,
                        monthlyRadiance = weatherData.irradiance,
                        solarArray = solarArray
                    )
                // dataMap skal mappe solarArray til listen med double verdier
                dataMap += solarArray to listOf(electricityProduction.values.toList()) as List<Double>
//                }

                _uiState.update { currentState ->
                    currentState.copy(
                        electricityProductionData = mapOf("Strømproduksjon" to electricityProduction.values.toList()),
                    )
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Feil ved innlasting av værdata", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Noe gikk galt med innhenting av data."
                    )
                }
            }
        }
    }

    fun selectSolarArray(solarArray: SolarArray) {
        viewModelScope.launch {
            if (uiState.value.solarArrays.value.isEmpty()) return@launch //Må velge fra liste
            try {
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedSolarArray = solarArray
                    )
                }
            } catch (ex: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Klarte ikke å velge solcelleanlegg"
                    )
                }
            }
        }
    }


    private fun loadData(days: Int, solarArray: SolarArray) {
        viewModelScope.launch {
            try {
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Laster inn strømpriser..."
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
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Klarte ikke å laste inn data"
                    )
                }
            } finally {
                val day = Scope.entries.filter { scopeToDays[it] == days }[0]
                _uiState.update { currentState ->
                    currentState.copy(
                        scope = day
                    )
                }
            }
        }
    }

    private fun seePrices(days: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Laster inn strømpriser"
                    )
                }

                val realPrice = realPriceMap[days]
                val solarPrice = solarPriceMap[days]

                if (realPrice != null) {
                    if (solarPrice != null) {
                        // Her vil jeg opprette nytt data object og oppdatere uistate med nytt obj
                        _uiState.update { currentState ->
                            currentState.copy(
                                //Her må det legges inn et Price-objekt
                            )
                        }
                    }
                }
            } catch (ex: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Klarte ikke å laste inn strømpriser"
                    )
                }
            } finally {
                val day = Scope.entries.filter { scopeToDays[it] == days }[0]
                _uiState.update { currentState ->
                    currentState.copy(
                        scope = day
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
                        loadingState = "Endrer tidsintervall"
                    )
                }
                seePrices(scopeToDays[scope]!!)
            } catch (ex: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        loadingState = "Feilet på å endre tidsintervall"
                    )
                }
            }
        }
    }
}