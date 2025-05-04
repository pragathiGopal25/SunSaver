package no.uio.ifi.in2000.team54.domain

import com.mapbox.geojson.Point

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
) {
    fun toPoint(): Point {
        return Point.fromLngLat(longitude, latitude)
    }
}
