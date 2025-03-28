package no.uio.ifi.in2000.team54.model.pvgis

data class SolarIrradianceData(
    val year: Int,
    val month: String,
    val irradiance: Double
)

data class LocationInfo(
    var latitude: Double,
    var longitude: Double,
    var radiationDb: String
)