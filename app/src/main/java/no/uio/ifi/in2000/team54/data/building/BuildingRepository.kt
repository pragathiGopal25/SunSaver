package no.uio.ifi.in2000.team54.data.building

import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.model.building.MapRoofSection
import no.uio.ifi.in2000.team54.model.building.Pos

class BuildingRepository() {

    private val dataSource = BuildingDataSource()

    suspend fun getAddressSuggestions(address: String): List<Address> {
        return dataSource.getAddressSuggestions(address)
    }

    suspend fun getNearestAddressToPos(pos: Pos): Address? {
        val address = dataSource.getAddressFromPos(pos)
        return address.minByOrNull { it.distanceFromPoint }
    }

    suspend fun getBuildingIds(address: Address): List<String> {
        val cadastreId = dataSource.getCadastreId(address) ?: return emptyList()
        val buildingIds = dataSource.getBuildingIds(cadastreId)
        return buildingIds.filter { !it.contains("-") }!!

    }

    suspend fun getRoofSections(address: Address): List<MapRoofSection> {
        val buildingIds = getBuildingIds(address)
        if (buildingIds.isEmpty()) {
            return emptyList()
        }

        return buildingIds.map { id ->
            dataSource.getRoofSections(id)
        }.flatten()
    }
}