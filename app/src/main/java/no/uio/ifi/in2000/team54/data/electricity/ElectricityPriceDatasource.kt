package no.uio.ifi.in2000.team54.data.electricity

import android.annotation.SuppressLint
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import no.uio.ifi.in2000.team54.model.electricity.ElectricityPriceInfo
import java.text.SimpleDateFormat
import java.util.Date

class ElectricityPriceDatasource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun getTodaysElectricityPrices(area: String): List<ElectricityPriceInfo> {
        val pattern = "yyyy/MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(Date())

        val response: List<ElectricityPriceInfo> =
            client.get("https://www.hvakosterstrommen.no/api/v1/prices/${date}_$area.json")
                .body()
        return response
    }

    suspend fun getElectricityPrices(
        area: String,
        date: String
    ): List<ElectricityPriceInfo> {

        val response: List<ElectricityPriceInfo> =
            client.get("https://www.hvakosterstrommen.no/api/v1/prices/${date}_$area.json")
                .body()

        return response
    }

}