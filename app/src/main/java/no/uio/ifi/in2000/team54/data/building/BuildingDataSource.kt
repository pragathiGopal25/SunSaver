package no.uio.ifi.in2000.team54.data.building

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
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
import no.uio.ifi.in2000.team54.ui.home.NetworkObserver

class BuildingDataSource(private val context: Context) {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val networkObserver = NetworkObserver(context)
    private suspend fun isOnline(): Boolean {

        return networkObserver.isConnected.first()
    }

    suspend fun getAddressSuggestions(address: String): Result<List<Address>> {

        if (!isOnline()) {
            return Result.failure(Exception("Manglende internettilgang."))
        }

        return try {
            val response = httpClient.get("https://ws.geonorge.no/adresser/v1/sok") {
                parameter("sok", address)
                parameter("fuzzy", true)
                parameter("utkoordsys", 4258)
                parameter("treffPerSide", 10)
                parameter("side", 0)
                parameter("asciiKompatibel", true)
            }

            if (response.status != HttpStatusCode.OK) {
                return Result.failure(Exception("Noe gikk galt under datainnhentingen."))

            } else {
                Result.success(response.body<AddressSuggestionsResponse>().suggestions)
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAddressFromPos(pos: Pos): Result<List<Address>> {

        if (!isOnline()) {
            return Result.failure(Exception("Manglende internettilgang."))
        }

        return try {
            val response = httpClient.get("https://ws.geonorge.no/adresser/v1/punktsok") {

                parameter("lat", pos.lat)
                parameter("lon", pos.lon)
                parameter("radius", 10)
                parameter("utkoordsys", 4258)
                parameter("side", 0)
                parameter("asciiKompatibel", true)
            }

            if (response.status != HttpStatusCode.OK) {
                Result.failure(Exception("Noe gikk galt under datainnhentingen."))

            } else {
                Result.success(response.body<AddressSuggestionsResponse>().suggestions)
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCadastreId(address: Address): Result<Long?> {

        if (!isOnline()) {
            return Result.failure(Exception("Manglende internettilgang."))
        }

        return try {
            val response = httpClient.get(

                "https://seeiendom.kartverket.no/api/matrikkelenhet/" +
                        "${address.communityNumber}/" +
                        "${address.cadastralNumber}/" +
                        "${address.propertyNumber}"
            )
            if (response.status != HttpStatusCode.OK) {
                Result.failure(Exception("Noe gikk galt under datainnhentingen."))

            } else {
                return Result.success(Json.parseToJsonElement(response.bodyAsText()).jsonObject["matrikkelenhetId"]?.jsonPrimitive?.long)
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBuildingIds(cadastreId: Long): Result<List<String>> {

        if (!isOnline()) {
            return Result.failure(Exception("Manglende internettilgang."))
        }

        return try {
            val response =
                httpClient.get("https://seeiendom.kartverket.no/api/bygningerForMatrikkelenhet/$cadastreId")

            if (response.status != HttpStatusCode.OK) {
                Result.failure(Exception("Noe gikk galt under datainnhentingen."))

            } else {
                Result.success(
                    Json.parseToJsonElement(response.bodyAsText()).jsonArray
                    .mapNotNull { it.jsonObject["bygningsnummer"] }
                    .map { it.jsonPrimitive.content }
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getRoofSections(buildingId: String, ): Result<List<MapRoofSection>> {

        if (!isOnline()) {

            return Result.failure(Exception("Manglende internettilgang."))
        }
        return try {
            val response =
                httpClient.get("https://sol-api.fjordkraft.no/roof-information/query-by-buildings") {
                    parameter("buildingIds", buildingId)
                }

            if (response.status != HttpStatusCode.OK) {
                Result.failure(Exception("Noe gikk galt under datainnhentingen."))

            } else {
                return Result.success(
                    response.body<RoofSectionsResponse>().roofSections
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
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