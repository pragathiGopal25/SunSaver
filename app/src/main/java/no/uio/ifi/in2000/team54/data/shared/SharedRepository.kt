package no.uio.ifi.in2000.team54.data.shared

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team54.domain.Coordinates
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType

class SharedRepository {
    // todo: Connect to Room database and store all values there
    // just for testing
    private val testRoofSection1 = RoofSection(
        area = 20.0,
        incline = 35.0,
        direction = 120.0,
        panels = 8,
        mapId = "rs1"
    )

    private val testRoofSection2 = RoofSection(
        area = 10.0,
        incline = 35.0,
        direction = 300.0,
        panels = 8,
        mapId = "rs2"
    )

    private val testSolarArray = SolarArray(
        name = "test1",
        panelType = SolarPanelType.ECONOMY,
        roofSections = listOf(testRoofSection1, testRoofSection2),
        coordinates = Coordinates(59.9423, 10.72)
    )

    private val solarArrays = MutableStateFlow<List<SolarArray>>(emptyList())
    val itemList: StateFlow<List<SolarArray>> = solarArrays.asStateFlow()

    fun addSolarArray(newSolarArray: SolarArray) {
        Log.i("testHash", System.identityHashCode(solarArrays.value).toString())
        solarArrays.value = (solarArrays.value + newSolarArray).toList()
        Log.i("test", "added$newSolarArray")
        // todo: check if exists, update then (probably need to add an id then)
    }
}

object RepositoryProvider {
    val sharedRepository = SharedRepository()
}

