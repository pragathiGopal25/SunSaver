package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.building.BuildingRepository
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.model.building.MapRoofSection
import no.uio.ifi.in2000.team54.model.building.Pos
import no.uio.ifi.in2000.team54.ui.network.NetworkObserver

class ManageSolarArrayViewModel( private val networkObserver: NetworkObserver) : ViewModel() {
    private val repository: BuildingRepository = BuildingRepository()
    private val _sunSaverRepository = RepositoryProvider.sunSaverRepository

    // allows us to access the value of the current solar array object, and keep the viewmodel updated on any changes
    // used only when we update
    private val _currentSolarArray = MutableStateFlow<SolarArray?>(null)
    val currentSolarArray: StateFlow<SolarArray?> = _currentSolarArray.asStateFlow()

    private val _mapAddress = MutableStateFlow(
        AddressState(null, null)
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
                if (!_isOnline.value) {
                    MapRoofSectionsState(emptyList(), true, "Ingen internettforbindelse.")

                }else {// we know the address isn't null here because we filter out all null addresses above
                    MapRoofSectionsState(repository.getRoofSections(state.address!!), false, null)
                }

            } catch (e: Exception) {
                delay(1000) // delayed so that the Building API gets time to respond
                // if it fails to get the roof information, don't display any in the map
                MapRoofSectionsState(emptyList(), true, "Noe gikk galt under datainnhentingen.")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MapRoofSectionsState(emptyList(), false, null)
        )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val mapSearchAddressSuggestions = _mapSearchAddress
        .debounce(250)
        .mapLatest { state ->

            try {
                if (!_isOnline.value) {
                    AddressSuggestionsState(emptyList(), "Ingen internettforbindelse.")

                } else {
                    val result = repository.getAddressSuggestions(state.query)
                    val suggestions = result.getOrNull() ?: emptyList()
                    AddressSuggestionsState(suggestions, null)
                }

            }catch(e: Exception) {
                AddressSuggestionsState(emptyList(), "Noe gikk galt med innhenting av data")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AddressSuggestionsState(emptyList(),null)
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
    fun updateSolarArrayAddress(solarArray: SolarArray?) {
        // Update the search address when selecting a solar array to edit
        val pos: Pos = Pos.fromPoint(solarArray?.coordinates!!.toPoint())
        queryAddressAtPos(pos)
        _mapSearchAddress.value = SearchAddressState(solarArray.address)
    }

    fun addSolarArray(newSolarArray: SolarArray) {
        viewModelScope.launch {
            _sunSaverRepository.addSolarArray(newSolarArray)
        }
    }
    fun queryAddressAtPos(pos: Pos) {
        viewModelScope.launch {

            if (!_isOnline.value) {

                _mapAddress.value = AddressState(null, "Ingen internettforbindelse.")
                return@launch
            }
            try {

                val address = repository.getNearestAddressToPos(pos) ?:  run {
                    _mapAddress.value = AddressState(null, "Fant ingen adresse for posisjonen.")
                    return@launch
                }

                setSearchAddress(address.toFormatted())
                setMapAddress(address)

            } catch (e: Exception) {

                _mapAddress.value = AddressState(null, "Noe gikk galt med innhenting av data")
            }
        }
    }

    // To Update the roof sections and other values when user edits
    fun updateSolarArray(newSolarArray: SolarArray){
        viewModelScope.launch {
            _sunSaverRepository.updateSolarArray(newSolarArray)
        }
    }

    fun getSolarArray(id: Long){
        viewModelScope.launch {
            _currentSolarArray.value = _sunSaverRepository.getAllSolarArrays()
                .filter { it.isNotEmpty() }
                .first()
                .find { it.id == id }
        }
    }

    fun resetUpdSolarArray() {
        _currentSolarArray.value = null
    }
}

data class AddressState(
    val address: Address?,
    val errorMessage: String?
)
data class MapRoofSectionsState(
    val roofSections: List<MapRoofSection>,
    val isError: Boolean,
    val errorMessage: String?
)
data class SearchAddressState(
    val query: String
)
data class AddressSuggestionsState(
    val suggestions: List<Address>,
    val errorMessage: String?

)