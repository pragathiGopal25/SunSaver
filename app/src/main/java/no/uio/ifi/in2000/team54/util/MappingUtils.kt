package no.uio.ifi.in2000.team54.util

import no.uio.ifi.in2000.team54.database.SolarArrayEntity
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType

fun toDomain(entity: SolarArrayWithRoofSections): SolarArray {
    return SolarArray(
        id = 0,
        name = "",
        panelType = SolarPanelType.ECONOMY,
        roofSections = listOf(),
        coordinates = Coordinates(1.0, 2.0),
        powerConsumption = 0.0,
    )
}

fun toEntity(solarArray: SolarArray): SolarArrayWithRoofSections {
    return SolarArrayWithRoofSections(
        solarArray = SolarArrayEntity(
            id = 0,
            name = "",
            panelType = "",
            latitude = 1.0,
            longitude = 2.0,
            powerConsumption = 0.0
        ),
        roofSections = listOf(),
    )
}
