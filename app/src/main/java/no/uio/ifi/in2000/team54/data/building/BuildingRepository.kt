package no.uio.ifi.in2000.team54.data.building

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.model.building.MapRoofSection
import no.uio.ifi.in2000.team54.model.building.Pos

class BuildingRepository() {

    private val dataSource = BuildingDataSource()

    suspend fun getAddressSuggestions(address: String): Result<List<Address>> {
        return dataSource.getAddressSuggestions(address)
    }

    suspend fun getNearestAddressToPos(pos: Pos): Address? {
        val result = dataSource.getAddressFromPos(pos)
        if (result.isFailure) {
            return null
        }

        val address = result.getOrNull().orEmpty()
        return address.minByOrNull { it.distanceFromPoint }
    }

    suspend fun getBuildingIds(address: Address): List<String> {

        val result = dataSource.getCadastreId(address)
        val cadastreId = result.getOrNull() ?: return emptyList()

        val buildingResult = dataSource.getBuildingIds(cadastreId)

        return buildingResult.getOrNull().orEmpty().filter { !it.contains("-") }!!

    }

    suspend fun getRoofSections(address: Address): List<MapRoofSection> {

        val buildingIds = getBuildingIds(address)

        if (buildingIds.isEmpty()) {
            return emptyList()
        }

        return buildingIds.mapNotNull { id ->
            dataSource.getRoofSections(id).getOrNull()
        }.flatten()
    }
}