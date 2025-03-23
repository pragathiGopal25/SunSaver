package no.uio.ifi.in2000.team54.data.building

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import no.uio.ifi.in2000.team54.model.building.RoofSection

class BuildingDataSource {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getBuildingDataByCoordinate(lat: Double, lng: Double): JsonObject? {
        val response = this.httpClient.get("https://overpass-api.de/api/interpreter") {
            parameter(
                "data", """
                [out:json];
                (
                  way["building"]["ref:bygningsnr"](around:10, $lat, $lng);
                );
                out geom;
            """.trimIndent()
            )
        }

        if (response.status != HttpStatusCode.OK) {
            return null
        }
        println(response.request.url.toString())

        return Json.parseToJsonElement(response.bodyAsText()).jsonObject
    }

    suspend fun getRoofSections(buildingIds: List<Long>): List<RoofSection> {
        val response = this.httpClient.get("https://sol-api.fjordkraft.no/roof-information/query-by-buildings") {
            parameter("buildingIds", buildingIds.joinToString(","))
        }

        return response.body<RoofResponse>().roofSections
    }
}

@Serializable
data class RoofResponse(val roofSections: List<RoofSection>)