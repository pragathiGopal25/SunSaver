package no.uio.ifi.in2000.team54.model.building

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
class AddressSuggestion(
    @JsonNames("adressetekst")
    val address: String,
    @JsonNames("poststed")
    val area: String,
    @JsonNames("postnummer")
    val areaCode: String,
    @JsonNames("representasjonspunkt")
    val pos: Pos
) {

    fun toFormatted(): String {
        return "${address}, ${areaCode} ${area}"
    }
}