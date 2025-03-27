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
import no.uio.ifi.in2000.team54.model.building.AddressSuggestion
import no.uio.ifi.in2000.team54.model.building.MapRoofSection
import no.uio.ifi.in2000.team54.model.building.Pos

class ManageSolarArrayViewModel : ViewModel() {
    private val repository: BuildingRepository = BuildingRepository()

    private val _mapQueryPos = MutableStateFlow(
        PosState(null)
    )
    private val _mapAddress = MutableStateFlow(
        AddressState("")
    )

    val mapAddress: StateFlow<AddressState> = _mapAddress.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val mapRoofSections = _mapQueryPos
        .filter { state -> state.pos != null }
        .mapLatest { state ->
            val roofSections = repository.getRoofSections(state.pos!!.lat, state.pos.lon)
            RoofSectionsState(roofSections)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = RoofSectionsState(emptyList())
        )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val mapAddressSuggestions = _mapAddress
        .debounce(250)
        .mapLatest { state ->
            val suggestions = repository.getAddressSuggestions(state.address)
            AddressSuggestionsState(suggestions)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AddressSuggestionsState(emptyList())
        )


    fun setMapQueryPos(pos: Pos) {
        _mapQueryPos.value = _mapQueryPos.value.copy(
            pos = pos
        )
    }

    fun setMapAddress(address: String) {
        _mapAddress.value = _mapAddress.value.copy(
            address = address
        )
    }
}

data class PosState(
    val pos: Pos?
)

data class RoofSectionsState(
    val roofSections: List<MapRoofSection>
)

data class AddressState(
    val address: String
)

data class AddressSuggestionsState(
    val suggestions: List<AddressSuggestion>
)