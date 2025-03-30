package no.uio.ifi.in2000.team54.data.building

import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.model.building.MapRoofSection

class BuildingRepository {
    private val dataSource = BuildingDataSource()

    suspend fun getAddressSuggestions(address: String): List<Address> {
        return dataSource.getAddressSuggestions(address)
    }

    suspend fun getBuildingIds(address: Address): List<String> {
        val cadastreId = dataSource.getCadastreId(address) ?: return emptyList()
        return dataSource.getBuildingIds(cadastreId).filter { !it.contains("-") }
    }

    suspend fun getRoofSections(address: Address): List<MapRoofSection> {
        val buildingIds = getBuildingIds(address)
        if (buildingIds.isEmpty()) {
            return emptyList()
        }

        return buildingIds.flatMap { dataSource.getRoofSections(it) }.toList()
    }
}