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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.model.building.MapRoofSection
import no.uio.ifi.in2000.team54.model.building.Pos

class BuildingDataSource() {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getAddressSuggestions(address: String): List<Address> {
        try {
            val response = httpClient.get("https://ws.geonorge.no/adresser/v1/sok") {
                parameter("sok", address)
                parameter("fuzzy", true)
                parameter("utkoordsys", 4258)
                parameter("treffPerSide", 10)
                parameter("side", 0)
                parameter("asciiKompatibel", true)
            }

            return if (response.status != HttpStatusCode.OK) {
                emptyList()
            } else {
                response.body<AddressSuggestionsResponse>().suggestions
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getAddressFromPos(pos: Pos): List<Address> {
        try {
            val response = httpClient.get("https://ws.geonorge.no/adresser/v1/punktsok") {
                parameter("lat", pos.lat)
                parameter("lon", pos.lon)
                parameter("radius", 10)
                parameter("utkoordsys", 4258)
                parameter("side", 0)
                parameter("asciiKompatibel", true)
            }

            return if (response.status != HttpStatusCode.OK) {
                emptyList()
            } else {
                response.body<AddressSuggestionsResponse>().suggestions
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getCadastreId(address: Address): Long? {
        try {
            val response = httpClient.get(
                "https://seeiendom.kartverket.no/api/matrikkelenhet/" +
                        "${address.communityNumber}/" +
                        "${address.cadastralNumber}/" +
                        "${address.propertyNumber}"
            )
            return if (response.status != HttpStatusCode.OK) {
                null
            } else {
                Json.parseToJsonElement(response.bodyAsText()).jsonObject["matrikkelenhetId"]?.jsonPrimitive?.long
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun getBuildingIds(cadastreId: Long): List<String> {
        try {
            val response = httpClient.get("https://seeiendom.kartverket.no/api/bygningerForMatrikkelenhet/$cadastreId")
            return if (response.status != HttpStatusCode.OK) {
                emptyList()
            } else {
                Json.parseToJsonElement(response.bodyAsText()).jsonArray
                    .mapNotNull { it.jsonObject["bygningsnummer"] }
                    .map { it.jsonPrimitive.content }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    suspend fun getRoofSections(buildingId: String): List<MapRoofSection> {
        try {
            val response = httpClient.get("https://sol-api.fjordkraft.no/roof-information/query-by-buildings") {
                parameter("buildingIds", buildingId)
            }
            return if (response.status != HttpStatusCode.OK) {
                emptyList()
            } else {
                response.body<RoofSectionsResponse>().roofSections
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
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
    val suggestions: List<Address>
)