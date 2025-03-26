package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import no.uio.ifi.in2000.team54.data.building.BuildingRepository
import no.uio.ifi.in2000.team54.model.building.Pos
import no.uio.ifi.in2000.team54.model.building.MapRoofSection

class ManageSolarArrayViewModel : ViewModel() {
    private val repository: BuildingRepository = BuildingRepository()

    private val _pos = MutableStateFlow(
        PosState(
            pos = null
        )
    )

    val pos = _pos.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val roofSections = _pos
        .filter { state -> state.pos != null }
        .mapLatest { state ->
            val roofSections = repository.getRoofSections(state.pos!!.lat, state.pos.lng)
            RoofSectionsState(roofSections)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = RoofSectionsState(emptyList())
        )

    fun setPos(pos: Pos) {
        _pos.value = _pos.value.copy(
            pos = pos
        )
    }
}

data class PosState(
    val pos: Pos?
)

data class RoofSectionsState(
    val roofSections: List<MapRoofSection>
)