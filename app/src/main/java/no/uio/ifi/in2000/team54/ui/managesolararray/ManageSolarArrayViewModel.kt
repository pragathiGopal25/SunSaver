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
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.model.building.MapRoofSection

class ManageSolarArrayViewModel : ViewModel() {
    private val repository: BuildingRepository = BuildingRepository()
    private val _sharedRepository = RepositoryProvider.sharedRepository

    private val _currentSolarArray = MutableStateFlow<SolarArray?>(null)
    val currentSolarArray: StateFlow<SolarArray?> = _currentSolarArray.asStateFlow()



    private val _mapAddress = MutableStateFlow(
        AddressState(null)
    )
    private val _mapSearchAddress = MutableStateFlow(
        SearchAddressState("")
    )

    val mapAddress: StateFlow<AddressState> = _mapAddress.asStateFlow()
    val mapSearchAddress: StateFlow<SearchAddressState> = _mapSearchAddress.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val mapRoofSections = _mapAddress
        .filter { state -> state.address != null }
        .mapLatest { state ->
            try {
                // we know the address isn't null here because we filter out all null addresses above
                MapRoofSectionsState(repository.getRoofSections(state.address!!), false)
            } catch (e: Exception) {
                // if it fails to get the roof information, don't display any in the map
                MapRoofSectionsState(emptyList(), true)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MapRoofSectionsState(emptyList(), false)
        )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val mapSearchAddressSuggestions = _mapSearchAddress
        .debounce(250)
        .mapLatest { state ->
            val suggestions = try {
                repository.getAddressSuggestions(state.query)
            } catch (e: Exception) {
                emptyList() // could not find any addresses for the users input
            }
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

    fun setCurrentSolarArray(solarArray: SolarArray?) {
        _currentSolarArray.value = solarArray
        // Update the search address when selecting a solar array to edit
        _mapSearchAddress.value = SearchAddressState(solarArray?.address?.toFormatted() ?: "")
        _mapAddress.value = _mapAddress.value.copy(
            address = solarArray?.address
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

    // To Update the roof sections and other values when user edits
    fun updateSolarArray(newSolarArray: SolarArray){
        _sharedRepository.updateSolarArray(newSolarArray)
    }
}

data class AddressState(
    val address: Address?
)

data class MapRoofSectionsState(
    val roofSections: List<MapRoofSection>,
    val isError: Boolean
)

data class SearchAddressState(
    val query: String
)

data class AddressSuggestionsState(
    val suggestions: List<Address>,
)