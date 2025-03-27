package no.uio.ifi.in2000.team54.data.building

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import no.uio.ifi.in2000.team54.model.building.AddressSuggestion
import no.uio.ifi.in2000.team54.model.building.MapRoofSection

class BuildingDataSource {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getAddressSuggestions(address: String): List<AddressSuggestion> {
        val response = httpClient.get("https://ws.geonorge.no/adresser/v1/sok") {
            parameter("sok", address)
            parameter("fuzzy", true)
            parameter("utkoordsys", 4258)
            parameter("treffPerSide", 10)
            parameter("side", 0)
            parameter("asciiKompatibel", true)
        }

        if (response.status != HttpStatusCode.OK) {
            return emptyList()
        }

        return response.body<AddressSuggestionsResponse>().suggestions
    }

    suspend fun getBuildingDataByCoordinate(lat: Double, lng: Double): JsonObject? {
        val response = httpClient.get("https://overpass-api.de/api/interpreter") {
            parameter(
                "data", """
                [out:json];
                (
                  way["building"]["ref:bygningsnr"](around:10, $lat, $lng);
                );
                out tags;
            """.trimIndent()
            )
        }

        if (response.status != HttpStatusCode.OK) {
            return null
        }

        return Json.parseToJsonElement(response.bodyAsText()).jsonObject
    }

    suspend fun getRoofSections(buildingId: Long): List<MapRoofSection> {
        val response = httpClient.get("https://sol-api.fjordkraft.no/roof-information/query-by-buildings") {
            parameter("buildingIds", buildingId)
        }

        return response.body<RoofSectionsResponse>().roofSections
    }
}

@Serializable
data class RoofSectionsResponse(
    val roofSections: List<MapRoofSection>
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class AddressSuggestionsResponse(
    @JsonNames("adresser")
    val suggestions: List<AddressSuggestion>
)