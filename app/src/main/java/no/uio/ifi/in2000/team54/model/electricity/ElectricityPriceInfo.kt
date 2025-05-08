package no.uio.ifi.in2000.team54.model.electricity

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ElectricityPriceInfo @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("NOK_per_kWh")  val nokPrKiloWh: Double,
    @JsonNames("EUR_per_kWh") val eurPrKiloWh: Double,
    @JsonNames("EXR") val exchangeRate: Double,
    @JsonNames("time_start") val timeStart: String,
    @JsonNames("time_end") val timeEnd: String,
)