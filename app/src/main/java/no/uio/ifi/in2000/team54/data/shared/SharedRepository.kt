package no.uio.ifi.in2000.team54.data.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team54.domain.SolarArray

class SharedRepository {
    // todo: Connect to Room database and store all values there
    private val _solarArrays = MutableStateFlow<List<SolarArray>>(emptyList())
    val solarArrays: StateFlow<List<SolarArray>> = _solarArrays.asStateFlow()

    fun addSolarArray(newSolarArray: SolarArray) {
        _solarArrays.value = (_solarArrays.value + newSolarArray).toList()
        // todo: check if exists, update then (probably need to add an id then)
    }

    //TODO: update to ID
    fun updateSolarArray(newSolarArray: SolarArray) {
        val updatedList = _solarArrays.value.map { solarObj ->
            if (solarObj.name == newSolarArray.name) {
                newSolarArray
            } else {
                solarObj
            }
        }
        _solarArrays.value = updatedList
    }

    //TODO: update to ID
    fun removeSolarArray(solarArray: SolarArray) {
        _solarArrays.value = _solarArrays.value.filter { it.name != solarArray.name }
    }
}

object RepositoryProvider {
    val sharedRepository = SharedRepository()
}

