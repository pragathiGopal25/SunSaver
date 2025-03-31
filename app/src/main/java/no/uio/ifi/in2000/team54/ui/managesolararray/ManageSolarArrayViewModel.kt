package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import no.uio.ifi.in2000.team54.data.building.BuildingRepository
import no.uio.ifi.in2000.team54.data.shared.SharedRepository
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.model.building.MapRoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray

class ManageSolarArrayViewModel : ViewModel() {
    private val repository: BuildingRepository = BuildingRepository()
    private val _sharedRepository: SharedRepository = SharedRepository()

    private val _mapAddress = MutableStateFlow(
        AddressState(null)
    )
    private val _mapSearchAddress = MutableStateFlow(
        SearchAddressState("")
    )

    val mapAddress: StateFlow<SearchAddressState> = _mapSearchAddress.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val mapRoofSections = _mapAddress
        .filter { state -> state.address != null }
        .mapLatest { state ->
            val roofSections = repository.getRoofSections(state.address!!)
            MapRoofSectionsState(roofSections)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MapRoofSectionsState(emptyList())
        )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val mapSearchAddressSuggestions = _mapSearchAddress
        .debounce(250)
        .mapLatest { state ->
            val suggestions = repository.getAddressSuggestions(state.query)
            AddressSuggestionsState(suggestions)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AddressSuggestionsState(emptyList())
        )

    fun setMapAddress(address: Address) {
        _mapAddress.value = _mapAddress.value.copy(
            address = address
        )
    }

    fun setMapAddress(query: String) {
        _mapSearchAddress.value = _mapSearchAddress.value.copy(
            query = query
        )
    }

    fun addSolarArray(newSolarArray: SolarArray) {
        _sharedRepository.addSolarArray(newSolarArray)
    }
}

data class AddressState(
    val address: Address?
)

data class MapRoofSectionsState(
    val roofSections: List<MapRoofSection>
)

data class SearchAddressState(
    val query: String
)

data class AddressSuggestionsState(
    val suggestions: List<Address>
)