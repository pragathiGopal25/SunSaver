package no.uio.ifi.in2000.team54.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.electricity.ElectricityPriceDatasource
import no.uio.ifi.in2000.team54.data.electricity.ElectricityPriceRepository

class ElectricityPriceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PriceUiState(0.0, 0.0, 0.0, false))
    private val priceData = ElectricityPriceRepository(ElectricityPriceDatasource())
    val uiState: StateFlow<PriceUiState> = _uiState.asStateFlow()
    private val realPriceMap = mutableMapOf<Int, Double>()
    private val priceMap = mutableMapOf<Int, Double>()
    private val solarPriceMap = mutableMapOf<Int, Double>()

    init {
        seePrices(1)
        val list = listOf(1, 30, 365)
        for (day in list) {
            loadData(day)
        }
    }

    private fun loadData(days: Int) {
        viewModelScope.launch {
            try {
                _uiState.value =
                    _uiState.value.copy(
                        loading = true
                    )
                if (!priceMap.containsKey(days)) {
                    priceMap[days] = priceData.getPriceData(days, "NO1")
                    realPriceMap[days] = priceMap[days]!! * priceData.fakeAvgKwh()
                    solarPriceMap[days] = priceMap[days]!! * (priceData.fakeAvgKwh() - 20) //dummy
                }
            } finally {

            }
        }
    }

    private fun seePrices(days: Int) {
        viewModelScope.launch {
            try {
                _uiState.value =
                    _uiState.value.copy(
                        loading = true
                    )
                if (!priceMap.containsKey(days)) {
                    priceMap[days] = priceData.getPriceData(days, "NO1")
                    realPriceMap[days] = priceMap[days]!! * priceData.fakeAvgKwh()
                    solarPriceMap[days] = priceMap[days]!! * (priceData.fakeAvgKwh() - 20) //dummy
                }

                val realPrice = realPriceMap[days]!!
                val solarPrice = solarPriceMap[days]!!

                _uiState.value =
                    _uiState.value.copy(
                        realPrice = (Math.round(realPrice * 10) / 10.0),
                        solarPrice = (Math.round(solarPrice * 10) / 10.0),
                        saved = Math.round((realPrice - solarPrice) * 10) / 10.0
                    )
            } finally {
                _uiState.value =
                    _uiState.value.copy(
                        loading = false
                    )
            }
        }
    }

    fun changeTimeScope(scope: Scope) {
        val map = mapOf(Scope.DAY to 1, Scope.MONTH to 30, Scope.YEAR to 365)
        viewModelScope.launch {
            try {
                seePrices(map[scope]!!)
            } finally {

            }
        }
    }
}

data class PriceUiState(
    val realPrice: Double,
    val solarPrice: Double,
    val saved: Double,
    val loading: Boolean
)

enum class Scope {
    DAY, MONTH, YEAR
}