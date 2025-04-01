package no.uio.ifi.in2000.team54.data.frost

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import android.util.Base64
import android.util.Log
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.model.frost.ObservationData
import no.uio.ifi.in2000.team54.model.frost.ObservationResponse
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


    private var sensorId = ""
    private var storedLatitude = 0.0
    private var storedLongitude = 0.0

    private suspend fun fetchNearestSource(coordinates:Coordinates): String {

        val response: HttpResponse = client.get("https://frost.met.no/sources/v0.jsonld?geometry=nearest(POINT(${coordinates.longitude}%20${coordinates.latitude}))") {
            header(HttpHeaders.Authorization, authHeader)
            header(HttpHeaders.Accept, "application/json")
        }

        val body: List<SensorSystem> = response.body<SourceResponse>().data

        return body[0].id
    }

    suspend fun fetchObservationDataFromFrost(
        coordinates: Coordinates,
        elementName: String,
        referenceTime: String
    ): List<ObservationData> {
        if(sensorId == "") {
            sensorId = fetchNearestSource(coordinates)
            storedLatitude = coordinates.latitude
            storedLongitude = coordinates.longitude
        }
        sensorId = "SN18700"

        if (coordinates.latitude != storedLatitude || coordinates.longitude != storedLongitude) { // coordiantes changed, so sensor needs to be updated.
            sensorId = fetchNearestSource(coordinates)
            storedLatitude = coordinates.latitude
            storedLongitude = coordinates.longitude
        }

        var url = "https://frost.met.no/observations/v0.jsonld?sources=$sensorId&referencetime=$referenceTime&elements=$elementName"

        if (elementName == "mean(surface_downwelling_shortwave_flux_in_air%20PT1H)") {
            Log.i("testMan", "man")
            url = "$url&qualities=0" // no data of other qualities
        }
        Log.i("testLink", url)

        val response: HttpResponse = client.get(url) {
            header(HttpHeaders.Authorization, authHeader)
            header(HttpHeaders.Accept, "application/json")
        }

        val observationResponse: ObservationResponse = response.body()

        return observationResponse.data
    }
}