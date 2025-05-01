package no.uio.ifi.in2000.team54.util

import no.uio.ifi.in2000.team54.database.RoofSectionEntity
import no.uio.ifi.in2000.team54.database.SolarArrayEntity
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType

fun toDomain(entity: SolarArrayWithRoofSections): SolarArray {
    val solarArray = entity.solarArray

    return SolarArray(
        id = solarArray.id,
        name = solarArray.name,
        panelType = SolarPanelType.fromDisplayName(solarArray.panelType),
        coordinates = Coordinates(
            latitude = solarArray.latitude,
            longitude = solarArray.longitude
        ),
        powerConsumption = solarArray.powerConsumption,
        address = solarArray.address,
        roofSections = entity.roofSections.map {
            RoofSection(
                id = it.roofSectionId,
                area = it.area,
                incline = it.incline,
                direction = it.direction,
                panels = it.panels,
                mapId = it.mapId
            )
        }
    )
}

fun toEntity(solarArray: SolarArray): SolarArrayWithRoofSections {

    return SolarArrayWithRoofSections(
        solarArray = SolarArrayEntity(
            id = solarArray.id ?: 0,
            name = solarArray.name,
            panelType = solarArray.panelType.displayName,
            latitude = solarArray.coordinates.latitude,
            longitude = solarArray.coordinates.longitude,
            powerConsumption = solarArray.powerConsumption,
            address = solarArray.address
        ),
        roofSections = solarArray.roofSections.map {
            RoofSectionEntity(
                roofSectionId = it.id ?: 0,
                solarArrayId = 0, // data source sets it
                area = it.area,
                incline = it.incline,
                direction = it.direction,
                panels = it.panels,
                mapId = it.mapId
            )
        },
    )
}
