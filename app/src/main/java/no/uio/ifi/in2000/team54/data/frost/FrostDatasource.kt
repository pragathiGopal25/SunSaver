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
    private val encoded: String = Base64.encodeToString(raw.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    private val authHeader = "Basic $encoded"


    private var sensorId = ""
    private var storedLatitude = 0.0
    private var storedLongitude = 0.0

    // calculate distance between two coordinates in on a globe:
    // Haversine formula: https://community.esri.com/t5/coordinate-reference-systems-blog/distance-on-a-sphere-the-haversine-formula/ba-p/902128
    private suspend fun calculateDistanceBetweenCoords(coordinates1:Coordinates, coordinates2: Coordinates): Double  {
        val earthRadius = 6371.0 // in km

        val distanceBetweenLats = Math.toRadians(coordinates2.latitude - coordinates1.latitude)
        val distanceBetweenLongs = Math.toRadians(coordinates2.longitude - coordinates1.longitude)

        // a represents the square of half of the chord length between two points on a sphere
        // helps measure how far apart two points are on the curved surface of the Earth
        val a = sin(distanceBetweenLats/2).pow(2) + cos(Math.toRadians(coordinates1.latitude)) * cos(Math.toRadians(coordinates2.latitude)) * sin(distanceBetweenLongs/2).pow(2)

        // c is the central angle between the two points which is measured in radians
        val c = 2* atan2(sqrt(a), sqrt(1 -a))

        return earthRadius * c
    }

    private suspend fun fetchNearestSource(coordinates:Coordinates, elementName: String? =null): String? {

        if (elementName == null) {

            val response: HttpResponse = client.get("https://frost.met.no/sources/v0.jsonld?geometry=nearest(POINT(${coordinates.longitude}%20${coordinates.latitude}))") {
                header(HttpHeaders.Authorization, authHeader)
                header(HttpHeaders.Accept, "application/json")
            }

            if (response.status.value  != 200) {
                return null
            } else {
                val body: List<SensorSystem> = response.body<SourceResponse>().data
                return body[0].id
            }

        } else {

            val response: HttpResponse = client.get("https://frost.met.no/sources/v0.jsonld?types=SensorSystem&elements=$elementName") {
                header(HttpHeaders.Authorization, authHeader)
                header(HttpHeaders.Accept, "application/json")
            }

            println(elementName.toString())



            val body: List<SensorSystem> = response.body<SourceResponse>().data
            var nearestDistance = Double.MAX_VALUE
            var nextNearestSensor = ""

            body.forEach { sensor ->
                if (sensor.id.contains("SN")) {
                    val calcDist = calculateDistanceBetweenCoords(
                        Coordinates(sensor.geometry.coordinates[0], sensor.geometry.coordinates[1]),
                        coordinates
                    )
                    if (calcDist < nearestDistance) {
                        nearestDistance = calcDist
                        nextNearestSensor = sensor.id
                    }
                }
            }

            Log.i("testingend", "finished here")
            return nextNearestSensor
        }

    }


    suspend fun fetchObservationDataFromFrost(
        coordinates: Coordinates,
        elementName: String,
        referenceTime: String
    ): List<ObservationData> {
        if(sensorId == "" || coordinates.latitude != storedLatitude || coordinates.longitude != storedLongitude) {

           if (fetchNearestSource(coordinates) ==  null) {
               sensorId = fetchNearestSource(coordinates, elementName).toString()
           } else {
               sensorId = fetchNearestSource(coordinates).toString()
           }
            storedLatitude = coordinates.latitude
            storedLongitude = coordinates.longitude
        }
       // sensorId = "SN18700"


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

        if (response.status.value != 200) {
            sensorId = fetchNearestSource(coordinates, elementName).toString()
            return fetchObservationDataFromFrost(coordinates, elementName, referenceTime)
        }
        val observationResponse: ObservationResponse = response.body()

        return observationResponse.data


    }
}