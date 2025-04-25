package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.util.toDomain
import no.uio.ifi.in2000.team54.util.toEntity
import org.junit.Test
import org.junit.Assert.*


class MappingTest {

    // from SolarArrayWithRoofSections to SolarArray
    @Test
    fun mapEntityToSolarArray() {
        val solarArray: SolarArray = toDomain(FakeSolarArrayEntity.solarArrayWithRoofSections)

        // assert solarArray itself
        assertEquals(1, solarArray.id)
        assertEquals("test1", solarArray.name)
        assertEquals(SolarPanelType.PERFORMANCE, solarArray.panelType)
        assertEquals(Coordinates(latitude = 59.899563, longitude = 10.485503),
            solarArray.coordinates)

        // assert RoofSections
        assertEquals(2, solarArray.roofSections.size)

        val roofSections = solarArray.roofSections.sortedBy { it.mapId }
        val roofSection1 = roofSections[0]
        assertEquals(1, roofSection1.id)
        assertEquals(20.0, roofSection1.area, 0.0)
        assertEquals(10, roofSection1.panels)

        val roofSection2 = roofSections[0]
        assertEquals(2, roofSection2.id)
        assertEquals(50.0, roofSection2.direction, 0.0)
        assertEquals(8, roofSection2.panels)
    }

    @Test
    fun mapSolarArrayToEntity() {
        val solarArrayWithRoofSections = toEntity(FakeSolarArrayDomain.solarArray)
        val solarArray = solarArrayWithRoofSections.solarArray
        assertEquals(0, solarArray.id) // expects it to set id to 0 if it is null
        assertEquals("test1", solarArray.name)
        assertEquals("Performance", solarArray.panelType)
        assertEquals(59.899563, solarArray.latitude, 0.0)
        assertEquals(10.485503, solarArray.longitude, 0.0)

        val roofSections = solarArrayWithRoofSections.roofSections.sortedBy { it.mapId }
        val roofSection1 = roofSections[0]
        assertEquals(1, roofSection1.roofSectionId)
        assertEquals(20.0, roofSection1.area, 0.0)
        assertEquals(10, roofSection1.panels)

        val roofSection2 = roofSections[0]
        assertEquals(0, roofSection2.roofSectionId)
        assertEquals(50.0, roofSection2.direction, 0.0)
        assertEquals(8, roofSection2.panels)
    }
}