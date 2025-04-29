package no.uio.ifi.in2000.team54.data.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.util.toDomain
import no.uio.ifi.in2000.team54.util.toEntity
import javax.inject.Inject

interface ISunSaverRepository {
    fun getAllSolarArrays(): Flow<List<SolarArray>>
    suspend fun addSolarArray(solarArray: SolarArray)
    suspend fun deleteSolarArray(solarArray: SolarArray)
    suspend fun updateSolarArray(solarArray: SolarArray)
}
class SharedRepository @Inject constructor(
    private val datasource: ISunSaverDatasource
): ISunSaverRepository {
    private val _solarArrays = MutableStateFlow<List<SolarArray>>(emptyList())
    val solarArrays: StateFlow<List<SolarArray>> = _solarArrays.asStateFlow()

    override fun getAllSolarArrays(): Flow<List<SolarArray>> {
        // transforming the flow of SolarArrayWithRoofSections to a flow of SolarArray
        return datasource.getAllSolarArrays().map { list ->
            list.map { toDomain(it) }
        }
    }

    // maps to SolarArrayWithRoofsections and calls add in Datasource
    override suspend fun addSolarArray(solarArray: SolarArray) {
        datasource.insert(toEntity(solarArray))
    }

    override suspend fun deleteSolarArray(solarArray: SolarArray) {
        datasource.delete(toEntity(solarArray))
    }

    override suspend fun updateSolarArray(solarArray: SolarArray) {
        datasource.update(toEntity(solarArray))
    }

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
}

object RepositoryProvider {
    lateinit var sharedRepository: SharedRepository
}
