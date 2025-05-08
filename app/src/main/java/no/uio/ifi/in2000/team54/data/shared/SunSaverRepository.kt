package no.uio.ifi.in2000.team54.data.shared

import kotlinx.coroutines.flow.Flow
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

class SunSaverRepository @Inject constructor(
    private val datasource: ISunSaverDatasource
) : ISunSaverRepository {
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
}