package no.uio.ifi.in2000.team54.ui.managesolararray

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.building.BuildingRepository
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.model.building.MapRoofSection
import no.uio.ifi.in2000.team54.model.building.Pos
import no.uio.ifi.in2000.team54.ui.home.NetworkObserver

class ManageSolarArrayViewModel(
    private val networkObserver: NetworkObserver

) : ViewModel() {

    private val repository: BuildingRepository = BuildingRepository()
    private val _sharedRepository = RepositoryProvider.sharedRepository

    // allows us to access the value of the current solar array object, and keep the viewmodel updated on any changes
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

    private val _isOnline = MutableStateFlow(true)
    val isOnline = _isOnline.asStateFlow()

    init {

        _isOnline.value = networkObserver.isNetworkAvailable()

        observeNetwork()
    }

    fun observeNetwork()  {

        viewModelScope.launch {

            networkObserver.isConnected.collectLatest { connected ->
                _isOnline.value = connected
            }
        }
    }

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
            val result = repository.getAddressSuggestions(state.query)
            val suggestions = result.getOrNull() ?: emptyList()

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
    fun setSearchAddress(query: String) {
        _mapSearchAddress.value = _mapSearchAddress.value.copy(
            query = query
        )
    }
    // used in SearchField method, and it allows the ui to remember the map address when navigating between screens
    fun setCurrentSolarArray(solarArray: SolarArray?) {
        _currentSolarArray.value = solarArray
        // Update the search address when selecting a solar array to edit
        _mapSearchAddress.value = SearchAddressState(solarArray?.address?.toFormatted() ?: "")
        _mapAddress.value = _mapAddress.value.copy(
            address = solarArray?.address
        )

    }
    fun addSolarArray(newSolarArray: SolarArray) {
        _sharedRepository.addSolarArray(newSolarArray)
    }
    fun queryAddressAtPos(pos: Pos) {
        viewModelScope.launch {
            val address = repository.getNearestAddressToPos(pos) ?: return@launch
            setSearchAddress(address.toFormatted())
            setMapAddress(address)
        }
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