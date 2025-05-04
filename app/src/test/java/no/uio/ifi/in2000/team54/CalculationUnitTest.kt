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
            powerConsumption = 10.0,
            address = "Test Address 333"
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

         val monthlySunhours = mapOf( // todo: Get from datasource!!
            "01" to 74.0,
            "02" to 90.0,
            "03" to 150.0,
            "04" to 200.0,
            "05" to 210.0,
            "06" to 230.0,
            "07" to 210.0,
            "08" to 180.0,
            "09" to 150.0,
            "10" to 90.0,
            "11" to 60.0,
            "12" to 55.0
        )


        // act
        val expectedResult = mapOf(
            "01" to 0.23759735999999995,
            "02" to 0.23759735999999995,
            "03" to 0.25309284,
            "04" to 0.25309284,
            "05" to 0.25309284,
            "06" to 0.25309284,
            "07" to 0.25309284,
            "08" to 0.25309284,
            "09" to 0.25309284,
            "10" to 0.25309284,
            "11" to 0.23759735999999995,
            "12" to 0.23759735999999995
        )

        val result = calculateMonthlyElectricityProduction(
            monthlyCloud = monthlyCloud,
            monthlySnow = monthlySnow,
            monthlyRadiance = monthlyRadiance,
            monthlyTemperatures = monthlyTemps,
            monthlySunhours = monthlySunhours,
            solarArray = testArray
        )

        println(result.toString())

        // assert
        expectedResult.forEach {
            assertEquals(it.value, result[it.key]!!, 0.001)
        }
    }
}