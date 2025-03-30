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

    private val _uiState = MutableStateFlow(PriceUiState(0.0, 0.0, 0.0))
    private val priceData = ElectricityPriceRepository(ElectricityPriceDatasource())
    val uiState: StateFlow<PriceUiState> = _uiState.asStateFlow()

    init {
        seePrices()
    }

    fun seePrices() {
        viewModelScope.launch {
            try {
                val realPrice = priceData.absPrice()
                val solarPrice = priceData.absPriceSolar()
                _uiState.value =
                    _uiState.value.copy(
                        realPrice = (Math.round(realPrice * 10) / 10.0),
                        solarPrice = (Math.round(solarPrice * 10) / 10.0),
                        saved = Math.round((realPrice - solarPrice) * 10) / 10.0
                    )
            } finally {

            }
        }
    }
}

data class PriceUiState(
    val realPrice: Double,
    val solarPrice: Double,
    val saved: Double
)
