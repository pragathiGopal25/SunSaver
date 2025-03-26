package no.uio.ifi.in2000.team54.data.building

import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import no.uio.ifi.in2000.team54.model.building.MapRoofSection

class BuildingRepository {
    private val dataSource = BuildingDataSource()

    suspend fun getNorwayBuildingId(lat: Double, lng: Double): List<Long> {
        val buildingData = dataSource.getBuildingDataByCoordinate(lat, lng) ?: return emptyList()
        val elements = buildingData["elements"] ?: return emptyList()

        return elements.jsonArray
            .map { it.jsonObject["tags"] }
            .filter { it != null && it.jsonObject["ref:bygningsnr"] != null }
            .map { it!!.jsonObject["ref:bygningsnr"]!!.jsonPrimitive.long }
            .toList()
    }

    suspend fun getRoofSections(lat: Double, lng: Double): List<MapRoofSection> {
        val buildingIds = listOf(80211163L)//getNorwayBuildingId(lat, lng)
        if (buildingIds.isEmpty()) {
            return emptyList()
        }

        return buildingIds.flatMap { dataSource.getRoofSections(it) }.toList()
    }

    suspend fun getRoofSections(buildingId: Long): List<MapRoofSection> {
        return dataSource.getRoofSections(buildingId)
    }
}