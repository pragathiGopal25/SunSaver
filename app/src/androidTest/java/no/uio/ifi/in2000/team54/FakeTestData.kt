package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.database.RoofSectionEntity
import no.uio.ifi.in2000.team54.database.SolarArrayEntity
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections

object TestSolarArray1 { // immutable test data
    val solarArray = SolarArrayEntity(
        name = "test1",
        panelType = "Premium",
        latitude = "59.899563", // should be transformed by repository
        longtitude = "10.485503"
    )

    private val roofSection1 = RoofSectionEntity(
        // id will be generated automatically
        solarPanelName = "", // datasource has to set it
        area = 23.0,
        incline = 35.0,
        direction = 120.0,
        panels = 9,
        mapId = "1",
    )

    private val roofSection2 = RoofSectionEntity(
        // id will be generated automatically
        solarPanelName = "", // datasource has to set it
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
        latitude = "59.805636",
        longtitude = "10.806756"
    )

    private val roofSection1 = RoofSectionEntity(
        solarPanelName = "",
        area = 20.0,
        incline = 15.0,
        direction = 23.0,
        panels = 6,
        mapId = "3",
    )

    private val roofSection2 = RoofSectionEntity(
        solarPanelName = "",
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