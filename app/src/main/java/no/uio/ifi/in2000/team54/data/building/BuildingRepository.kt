package no.uio.ifi.in2000.team54.data.building

import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

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

    suspend fun getRoofSections(lat: Double, lng: Double): List<RoofSection> {
        val buildingIds = this.getNorwayBuildingId(lat, lng)
        if (buildingIds.isEmpty()) {
            return emptyList()
        }

        return dataSource.getRoofSections(buildingIds.subList(0, 1))
    }

    suspend fun getRoofSections(buildingIds: List<Long>): List<RoofSection> {
        return dataSource.getRoofSections(buildingIds)
    }
}