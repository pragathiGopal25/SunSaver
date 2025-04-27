package no.uio.ifi.in2000.team54.data.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team54.domain.SolarArray
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
        TODO("Not yet implemented")
    }

    // maps to SolarArrayWithRoofsections and calls add in Datasource
    override suspend fun addSolarArray(solarArray: SolarArray) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSolarArray(solarArray: SolarArray) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSolarArray(solarArray: SolarArray) {
        TODO("Not yet implemented")
    }
}

object RepositoryProvider {
    lateinit var sharedRepository: SharedRepository
}
