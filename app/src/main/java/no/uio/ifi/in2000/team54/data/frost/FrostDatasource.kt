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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.model.frost.ObservationData
import no.uio.ifi.in2000.team54.model.frost.ObservationResponse
import no.uio.ifi.in2000.team54.model.frost.SensorSystem
import no.uio.ifi.in2000.team54.model.frost.SourceResponse
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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
    private val encoded: String =
        Base64.encodeToString(raw.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    private val authHeader = "Basic $encoded"


    private var storedLatitude = 0.0
    private var storedLongitude = 0.0

    private val nameMap = mapOf(
        "temp" to "mean(air_temperature%20P1M)",
        "cloud" to "mean(cloud_area_fraction%20P1D)",
        "snow" to "mean(snow_coverage_type%20P1M)",
        "radiation" to "mean(surface_downwelling_shortwave_flux_in_air%20PT1H)"
    )

    private var sensorMap: MutableMap<String, String> = mutableMapOf()

    private suspend fun fetchNearestSource(
        coordinates: Coordinates,
        element: String? = null,
        elementName: Map<String, String> = emptyMap()
    ): MutableMap<String, String> {

        var nameUrl = ""

        if (element == null) {
            nameMap.values.forEach { value ->
                nameUrl += if (value == nameMap.values.last()) {
                    value
                } else {
                    "$value%2C"
                }
            }
        } else {
            nameUrl = element
        }



        try {
            val response: HttpResponse =
                client.get("https://frost.met.no/sources/v0.jsonld?geometry=nearest(POINT(${coordinates.longitude}%20${coordinates.latitude}))&elements=$nameUrl&nearestmaxcount=1") {
                    header(HttpHeaders.Authorization, authHeader)
                    header(HttpHeaders.Accept, "application/json")
                }

            if (response.status.value != 200) { // Cannot find nearest sensor for ALL urls, try individual URLS now
                elementName.values.forEach { value ->
                    sensorMap[value] = fetchNearestSource(coordinates, value).get(value) ?: ""
                }

                return sensorMap

            } else {
                val body: List<SensorSystem> = response.body<SourceResponse>().data

                if (element == null) {
                    elementName.values.forEach { value ->
                        sensorMap[value] = body[0].id
                    }

                } else {
                    sensorMap[element] = body[0].id
                }

                return sensorMap
            }
        } catch (e: Exception) {
            Log.e("fetchNearestSource", "Error fetching nearest source: ${e.message}", e)

            return mutableMapOf()
        }

    }



    suspend fun fetchObservationDataFromFrost(
        coordinates: Coordinates,
        elementName: String,
        referenceTime: String
    ): List<ObservationData> {
        sensorMap = fetchNearestSource(coordinates, elementName = nameMap)

        if(coordinates.latitude != storedLatitude || coordinates.longitude != storedLongitude) {
            storedLatitude = coordinates.latitude
            storedLongitude = coordinates.longitude
        }


        var sensorId = sensorMap[elementName]

        var url = "https://frost.met.no/observations/v0.jsonld?sources=$sensorId&referencetime=$referenceTime&elements=$elementName"

        if (elementName == "mean(surface_downwelling_shortwave_flux_in_air%20PT1H)") {
            Log.i("testMan", "man")
            url = "$url&qualities=0" // no data of other qualities
        }


        var response: HttpResponse

        try {
            response = client.get(url) {
                header(HttpHeaders.Authorization, authHeader)
                header(HttpHeaders.Accept, "application/json")
            }
        } catch (e: Exception) {
            Log.e("fetchObservationData", "Error fetching data for timeline $referenceTime", e)
            return emptyList()  // Return an empty list or handle the error
        }

        if (response.status.value != 200) { // temporary. If sensor doesnt have data for given reference time, just utilize main sensor.
            sensorId = "SN18700"
            url = "https://frost.met.no/observations/v0.jsonld?sources=$sensorId&referencetime=$referenceTime&elements=$elementName"

            response =  client.get(url) {
                header(HttpHeaders.Authorization, authHeader)
                header(HttpHeaders.Accept, "application/json")
            }
        }
        val observationResponse: ObservationResponse = response.body()

        return observationResponse.data


    }
}