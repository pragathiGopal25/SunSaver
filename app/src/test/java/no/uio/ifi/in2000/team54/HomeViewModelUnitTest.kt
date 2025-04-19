package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team54.ui.home.HomeScreenViewModel.PriceUiState
import no.uio.ifi.in2000.team54.ui.home.HomeScreenViewModel.Scope
import no.uio.ifi.in2000.team54.util.calculateElectricityProduction
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeViewModelUnitTest {

    @Test
    fun testCalculationShouldReturnTrue() {
        //TODO arrange
        val expectedResult = 0

        val testRoofSection = RoofSection(
            area = 30.0,
            incline = 45.0,
            direction = 30.0,
            panels = 1,
            mapId = "3403043"
        )
        val testList = listOf(testRoofSection)
        val coordinates = Coordinates(
            55.5, 55.5
        )
        val testArray = SolarArray(
            name = "test",
            panelType = SolarPanelType.PREMIUM,
            roofSections = testList,
            coordinates = coordinates,
            powerConsumption = 30.0
        )

        val monthlyCloud: Map<String, Double> = emptyMap()
        val monthlySnow: Map<String, Double> = emptyMap()
        val monthlyRadiance: Map<String, Double> = emptyMap()
        val monthlyTemps: Map<String, Double> = emptyMap()

        //TODO act
        val result = calculateElectricityProduction(
            monthlyCloud = monthlyCloud,
            monthlySnow = monthlySnow,
            monthlyRadiance = monthlyRadiance,
            monthlyTemps = monthlyTemps,
            solarArray = testArray
        )

        //TODO assert
    }

    @Test
    fun testCalculationShouldReturnFalse() {
        //TODO arrange

        //TODO act

        //TODO assert
    }
}