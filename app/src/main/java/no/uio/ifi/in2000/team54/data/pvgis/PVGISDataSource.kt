package no.uio.ifi.in2000.team54.data.pvgis

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team54.model.pvgis.SolarIrradianceData

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


    suspend fun fetchMonthlyRadiation (latitude:Double, longitude:Double): List<SolarIrradianceData> {
        val response: HttpResponse = client.get("https://re.jrc.ec.europa.eu/api/MRcalc?lat=$latitude&lon=$longitude&horirrad=1")
            // retrieves: Output horizontal plane irradiation.
            // this is essentially the total amount of solar energy that hits a horizontal surface.
            // units: kWh/m²
        val rawData = response.bodyAsText()
        return formatMonthlyRadiationData(rawData)
    }

    private fun formatMonthlyRadiationData(data: String): List<SolarIrradianceData> {
        // data in the form of a table, so replacing the tabs and newlines here.
        val cleanedData = data.replace("\t", " ").replace("\n", " ").trim()

        // Formatting the date expression in the table.
            // d{4} = date (eg. 2005)
            // \s = represents space (as the values in the table are spaced apart)
            // [A-Za-z]+ = one or more character/letter
            // \d+ = one or more digits
            // \. = decimal point
            // .toRegex() =. converts everything into a regular expression. Note that a regular expression is: a sequence of symbols and characters expressing a string or pattern to be searched for within a longer piece of text.
        val dateExpression = """(\d{4})\s+([A-Za-z]+)\s+(\d+\.\d+)""".toRegex()

        // Iterates through the data to find all instance of the dates that match the dateExpression. It then returns an object of that expression (matchResult)
        // each date is then split into the year, month and irradiance respectively.
        val irradianceData = dateExpression.findAll(cleanedData).map { matchResult ->
            val year = matchResult.groupValues[1].toInt() // Extract year
            val month = matchResult.groupValues[2] // Extract month
            val irradiance = matchResult.groupValues[3].toDouble() // Extract irradiance value

            SolarIrradianceData(year, month, irradiance) // creating an object of the SolarIrradianceData data class
        }.toList()

        Log.i("Error her", irradianceData.toString())
        return irradianceData
    }



}