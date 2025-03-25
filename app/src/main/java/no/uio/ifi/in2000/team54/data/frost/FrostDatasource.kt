package no.uio.ifi.in2000.team54.data.frost

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import android.util.Base64
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.team54.model.frost.SensorSystem
import no.uio.ifi.in2000.team54.model.frost.SourceResponse

class FrostDatasource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private val raw = "b8d04ecc-dc8a-40a9-942e-2acfb8aba15d" + ":" // client id for team54@uio.no
    private val encoded: String = Base64.encodeToString(raw.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    private val authHeader = "Basic $encoded"

    private suspend fun fetchNearestSource(latitude: String, longitude: String): String {

        val response: HttpResponse = client.get("https://frost.met.no/sources/v0.jsonld?geometry=nearest(POINT($latitude%20$longitude))") {
            header(HttpHeaders.Authorization, authHeader)
            header(HttpHeaders.Accept, "application/json")
        }

        val body: List<SensorSystem> = response.body<SourceResponse>().data

        return body[0].id
    }

    // Just to test connection (does nothing useful)
    suspend fun getSomethingFromFrost(latitude: String, longtitude: String): List<String> {

        @Serializable
        data class Test(val sourceId: String)
        @Serializable
        data class ResponseTest (val data: List<Test>)

        val source = fetchNearestSource(latitude, longtitude)

        val urlMock = "https://frost.met.no/observations/v0.jsonld?sources=$source&referencetime=2024-03-25%2F2025-03-25&elements=mean(cloud_area_fraction%20P1D)"
        val response: HttpResponse = client.get(urlMock) {
            header(HttpHeaders.Authorization, authHeader)
            header(HttpHeaders.Accept, "application/json")
        }

        val testObservations = response.body<ResponseTest>().data

        return testObservations.map {it.sourceId}
    }
}