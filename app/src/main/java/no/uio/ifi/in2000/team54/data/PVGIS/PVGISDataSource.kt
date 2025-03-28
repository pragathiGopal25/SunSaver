package no.uio.ifi.in2000.team54.data.PVGIS

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PVGISDataSource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }



    suspend fun fetchMonthlyRadiation (latitude:Double, longitude:Double) {
        val response: HttpResponse = client.get("https://re.jrc.ec.europa.eu/api/MRcalc?lat=$latitude&lon=$longitude&horirrad=1")

    }
}