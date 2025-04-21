package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.database.RoofSectionEntity
import no.uio.ifi.in2000.team54.database.SolarArrayEntity
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections

object FakeTestData { // immutable test data
    val solarArrayA = SolarArrayEntity(
        name = "test1",
        panelType = "Premium",
        latitude = "10.100", // should be transformed by repository
        longtitude = "59.1589"
    )

    private val roofSectionA1 = RoofSectionEntity(
        // id will be generated automatically
        solarPanelName = "", // datasource has to set it
        area = 23.0,
        incline = 35.0,
        direction = 120.0,
        panels = 9,
        mapId = "1",
    )

    private val roofSectionA2 = RoofSectionEntity(
        // id will be generated automatically
        solarPanelName = "", // datasource has to set it
        area = 14.0,
        incline = 35.0,
        direction = 210.0,
        panels = 2,
        mapId = "2",
    )

    val solarArrayWithRoofSections = SolarArrayWithRoofSections(
        solarArray = solarArrayA,
        roofSections = listOf(roofSectionA1, roofSectionA2)

    )
}