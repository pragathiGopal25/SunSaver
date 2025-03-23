package no.uio.ifi.in2000.team54.model.building

import com.mapbox.geojson.Point
import kotlinx.serialization.Serializable

@Serializable
class Pos(
    val lat: Double,
    val lng: Double
) {

    companion object {

        fun fromPoint(point: Point): Pos {
            return Pos(point.latitude(), point.longitude())
        }
    }
}