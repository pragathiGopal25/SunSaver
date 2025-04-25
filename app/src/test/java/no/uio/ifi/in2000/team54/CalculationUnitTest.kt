package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.util.calculateMonthlyElectricityProduction
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeViewModelUnitTest {

    @Test
    fun testCalculationShouldReturnTrue() {
        // arrange
        val testRoofSection = RoofSection(
            id = 1,
            area = 10.0,
            incline = 45.0,
            direction = 10.0,
            panels = 1,
            mapId = "1"
        )
        val testList = listOf(testRoofSection)
        val coordinates = Coordinates(
            55.5, 55.5
        )
        val testArray = SolarArray(
            id = 1,
            name = "test",
            panelType = SolarPanelType.PREMIUM,
            roofSections = testList,
            coordinates = coordinates,
            powerConsumption = 10.0
        )

        val monthlyCloud: Map<String, Double> = mapOf(
            "01" to 1.0,
            "02" to 1.0,
            "03" to 1.0,
            "04" to 1.0,
            "05" to 1.0,
            "06" to 1.0,
            "07" to 1.0,
            "08" to 1.0,
            "09" to 1.0,
            "10" to 1.0,
            "11" to 1.0,
            "12" to 1.0
        )
        val monthlySnow: Map<String, Double> = mapOf(
            "01" to 1.0,
            "02" to 1.0,
            "03" to 1.0,
            "04" to 1.0,
            "05" to 1.0,
            "06" to 1.0,
            "07" to 1.0,
            "08" to 1.0,
            "09" to 1.0,
            "10" to 1.0,
            "11" to 1.0,
            "12" to 1.0
        )
        val monthlyRadiance: Map<String, Double> = mapOf(
            "01" to 1.0,
            "02" to 1.0,
            "03" to 1.0,
            "04" to 1.0,
            "05" to 1.0,
            "06" to 1.0,
            "07" to 1.0,
            "08" to 1.0,
            "09" to 1.0,
            "10" to 1.0,
            "11" to 1.0,
            "12" to 1.0
        )
        val monthlyTemps: Map<String, Double> = mapOf(
            "01" to 1.0,
            "02" to 1.0,
            "03" to 1.0,
            "04" to 1.0,
            "05" to 1.0,
            "06" to 1.0,
            "07" to 1.0,
            "08" to 1.0,
            "09" to 1.0,
            "10" to 1.0,
            "11" to 1.0,
            "12" to 1.0
        )

        // act
        val expectedResult = mapOf(
            "01" to 0.0703444,
            "02" to 0.085554,
            "03" to 0.14259,
            "04" to 0.19012,
            "05" to 0.19962600000000003,
            "06" to 0.218638,
            "07" to 0.19962600000000003,
            "08" to 0.171108,
            "09" to 0.14259,
            "10" to 0.085554,
            "11" to 0.05703600000000001,
            "12" to 0.05228300000000001
        )
        val result = calculateMonthlyElectricityProduction(
            monthlyCloud = monthlyCloud,
            monthlySnow = monthlySnow,
            monthlyRadiance = monthlyRadiance,
            monthlyTemperatures = monthlyTemps,
            solarArray = testArray
        )

        // assert
        expectedResult.forEach {
            assertEquals(it.value, result[it.key]!!, 0.001)
        }
    }
}