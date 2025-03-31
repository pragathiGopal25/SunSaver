package no.uio.ifi.in2000.team54.model.building

import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.turf.TurfJoins
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class RoofSectionGeometry(
    val coordinates: List<List<List<Double>>>
) {

    fun contains(point: Point): Boolean {
        val polygon = Polygon.fromLngLats(listOf(toPoints()))
        return TurfJoins.inside(point, polygon)
    }

    fun toPoints(): List<Point> {
        val points = coordinates[0].map {
            Point.fromLngLat(it[0], it[1])
        }

        return points
    }
}