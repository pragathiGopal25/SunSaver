package no.uio.ifi.in2000.team54.data.electricity

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import no.uio.ifi.in2000.team54.model.electricity.ElectricityPriceInfo

class ElectricityPriceDatasource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getElectricityPrices(): List<ElectricityPriceInfo> {
        val response: List<ElectricityPriceInfo> = client.get("https://www.hvakosterstrommen.no/api/v1/prices/2025/03-20_NO1.json").body()
        return response
    }
}