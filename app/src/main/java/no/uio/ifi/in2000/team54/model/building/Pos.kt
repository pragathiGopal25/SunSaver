package no.uio.ifi.in2000.team54.model.building

import com.mapbox.geojson.Point
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import no.uio.ifi.in2000.team54.domain.Coordinates

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
class Pos(
    val lat: Double,
    val lon: Double
) {

    fun toPoint(): Point {
        return Point.fromLngLat(lon, lat)
    }

    fun toCoordinates(): Coordinates {
        return Coordinates(lat, lon)
    }

    companion object {

        fun fromPoint(point: Point): Pos {
            return Pos(point.latitude(), point.longitude())
        }
    }
}