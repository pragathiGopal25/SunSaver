package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.database.RoofSectionEntity
import no.uio.ifi.in2000.team54.database.SolarArrayEntity
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType

object FakeSolarArrayDomain {
    private val roofSection1 = RoofSection(
        id = 1,
        area = 20.0,
        incline = 15.0,
        direction = 230.0,
        panels = 10,
        mapId = "1"
    )
    private val roofSection2 = RoofSection(
        id = null, // new
        area = 10.0,
        incline = 15.0,
        direction = 50.0,
        panels = 8,
        mapId = "2"
    )

    val solarArray = SolarArray(
        id = null,
        name = "test1",
        panelType = SolarPanelType.PERFORMANCE,
        roofSections = listOf(roofSection1, roofSection2),
        coordinates = Coordinates(latitude = 59.899563, longitude = 10.485503),
        powerConsumption = 1200.0
    )
}

object FakeSolarArrayEntity {
    private val roofSection1 = RoofSectionEntity(
        roofSectionId = 1,
        solarArrayId = 1,
        area = 20.0,
        incline = 15.0,
        direction = 230.0,
        panels = 10,
        mapId = "1"
    )

    private val roofSection2 = RoofSectionEntity(
        roofSectionId = 2,
        solarArrayId = 1,
        area = 10.0,
        incline = 15.0,
        direction = 50.0,
        panels = 8,
        mapId = "2"
    )

    private val solarArray = SolarArrayEntity(
        id = 1,
        name = "test1",
        panelType = "Performance",
        latitude = 59.899563,
        longitude = 10.485503,
        powerConsumption = 0.0,
    )

    val solarArrayWithRoofSections = SolarArrayWithRoofSections(
        solarArray = solarArray,
        roofSections = listOf(roofSection1, roofSection2)
    )
}