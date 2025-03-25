package no.uio.ifi.in2000.team54.model.building

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class RoofSectionGeometry(
    val coordinates: List<List<List<Double>>>
)