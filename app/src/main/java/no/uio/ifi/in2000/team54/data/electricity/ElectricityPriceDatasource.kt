package no.uio.ifi.in2000.team54.data.electricity

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import no.uio.ifi.in2000.team54.model.electricity.ElectricityPriceInfo
import javax.inject.Inject

class ElectricityPriceDatasource @Inject constructor(private val client: HttpClient){

    suspend fun getElectricityPrices(area: String, date: String): List<ElectricityPriceInfo> {
        try {
            val response: List<ElectricityPriceInfo> = client
                .get("https://www.hvakosterstrommen.no/api/v1/prices/${date}_$area.json")
                .body()

            return response
        } catch (e: Exception) {
            Log.e("getElectricityPrices", "${e.message}")
            return emptyList()
        }
    }
    
}