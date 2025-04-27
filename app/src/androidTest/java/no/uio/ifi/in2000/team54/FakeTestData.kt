package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.database.RoofSectionEntity
import no.uio.ifi.in2000.team54.database.SolarArrayEntity
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType

// roofSectionId will be generated automatically
object TestSolarArray1 { // immutable test data
    private val solarArray = SolarArrayEntity(
        name = "test1",
        panelType = "Premium",
        latitude = 59.899563, // should be transformed by repository
        longitude = 10.485503,
        powerConsumption = 1200.0
    )

    private val roofSection1 = RoofSectionEntity(
        solarArrayId = -1, // will use to verify if insertion works as intended
        area = 23.0,
        incline = 35.0,
        direction = 120.0,
        panels = 9,
        mapId = "1",
    )

    private val roofSection2 = RoofSectionEntity(
        solarArrayId = -1,
        area = 14.0,
        incline = 35.0,
        direction = 210.0,
        panels = 2,
        mapId = "2",
    )

    val solarArrayWithRoofSections = SolarArrayWithRoofSections(
        solarArray = solarArray,
        roofSections = listOf(roofSection1, roofSection2)

    )
}

object TestSolarArray2 {
    // second solar array set
    private val solarArray = SolarArrayEntity(
        name = "test2",
        panelType = "Economy",
        latitude = 59.805636,
        longitude = 10.806756,
        powerConsumption = 1200.0
    )

    private val roofSection1 = RoofSectionEntity(
        solarArrayId = -1,
        area = 20.0,
        incline = 15.0,
        direction = 23.0,
        panels = 6,
        mapId = "3",
    )

    private val roofSection2 = RoofSectionEntity(
        solarArrayId = -1,
        area = 20.0,
        incline = 15.0,
        direction = 203.0,
        panels = 8,
        mapId = "4",
    )

    val solarArrayWithRoofSections = SolarArrayWithRoofSections(
        solarArray = solarArray,
        roofSections = listOf(roofSection1, roofSection2)

    )
}


object TestSolarArrayDomain1 {
    private val roofSection1 = RoofSection(
        id = null,
        area = 36.0,
        incline = 23.0,
        direction = 120.0,
        panels = 9,
        mapId = "1"
    )
    private val roofSection2 = RoofSection(
        id = null,
        area = 23.0,
        incline = 23.0,
        direction = 30.0,
        panels = 4,
        mapId = "2"
    )

    val solarArray = SolarArray(
        id = null,
        name = "test2",
        panelType = SolarPanelType.ECONOMY,
        roofSections = listOf(roofSection1, roofSection2),
        coordinates = Coordinates(latitude = 59.909563, longitude = 10.445503),
        powerConsumption = 300.0
    )
}

object TestSolarArrayDomain2 {
    private val roofSection1 = RoofSection(
        id = null,
        area = 10.0,
        incline = 13.0,
        direction = 122.0,
        panels = 3,
        mapId = "1"
    )

    val solarArray = SolarArray(
        id = null,
        name = "test3",
        panelType = SolarPanelType.PREMIUM,
        roofSections = listOf(roofSection1),
        coordinates = Coordinates(latitude = 59.902563, longitude = 10.345503),
        powerConsumption = 1245.0
    )
}
