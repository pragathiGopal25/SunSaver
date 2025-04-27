package no.uio.ifi.in2000.team54

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team54.data.shared.ISunSaverDatasource
import no.uio.ifi.in2000.team54.data.shared.SharedRepository
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// here we have to focus on mapping
class SunSaverRepositoryTest {
    private lateinit var fakeDatasource: FakeSunSaverDatasource
    private lateinit var sunSaverRepository: SharedRepository

    @Before
    fun setUp() {
        fakeDatasource = FakeSunSaverDatasource()
        sunSaverRepository = SharedRepository(fakeDatasource)
    }

    @Test // test mapping during insertion
    fun addSolarArray() = runTest {
        // add
        sunSaverRepository.addSolarArray(TestSolarArrayDomain1.solarArray)
        sunSaverRepository.addSolarArray(TestSolarArrayDomain2.solarArray)

        // verify mapping and insertion by looking into the saved solar arrays
        val list = fakeDatasource.getSavedSolarArrays()
        assertEquals(2, list.size)

        // assert mapping
        val insertedSolarArray = list[0]
        assertEquals(1L, insertedSolarArray.solarArray.id)
        assertEquals("test2", insertedSolarArray.solarArray.name)
        assertEquals("Economy", insertedSolarArray.solarArray.panelType)
        assertEquals(59.909563, insertedSolarArray.solarArray.latitude, 0.0)
        assertEquals(10.445503, insertedSolarArray.solarArray.longitude, 0.0)
        assertEquals(2, insertedSolarArray.roofSections.size)

        insertedSolarArray.roofSections.forEach {
            assertEquals(1L, it.solarArrayId)
        }

        val roofSection1 = insertedSolarArray.roofSections[0]
        assertEquals(1L, roofSection1.roofSectionId)
        assertEquals(36.0, roofSection1.area, 0.0)

        val roofSection2 = insertedSolarArray.roofSections[1]
        assertEquals(2L, roofSection2.roofSectionId)
        assertEquals(23.0, roofSection2.area, 0.0)

        val insertedSolarArray2 = list[1]
        // assert IDs
        assertEquals(2L, insertedSolarArray2.solarArray.id)
        assertEquals("test3", insertedSolarArray2.solarArray.name)

        insertedSolarArray2.roofSections.forEach {
            assertEquals(2L, it.solarArrayId)
        }
    }

    @Test
    fun getSolarArray() = runTest { // check mapping Domain -> Entity -> Domain
        sunSaverRepository.addSolarArray(TestSolarArrayDomain1.solarArray)
        sunSaverRepository.addSolarArray(TestSolarArrayDomain2.solarArray)

        val list = sunSaverRepository.getAllSolarArrays().first()
        assertEquals(2, list.size)

        val solarArray1 = list[0]
        assertEquals(1L, solarArray1.id)
        assertEquals(2, solarArray1.roofSections.size)
        assertEquals(TestSolarArrayDomain1.solarArray.coordinates, solarArray1.coordinates)

        val solarArray2 = list[1]
        assertEquals(2L, solarArray2.id)
        assertEquals(SolarPanelType.PREMIUM, solarArray2.panelType)

        assertEquals(1, solarArray2.roofSections.size)
        assertEquals(3L, solarArray2.roofSections[0].id)
    }

    @Test
    fun addAndDeleteSolarArray() = runTest {
        sunSaverRepository.addSolarArray(TestSolarArrayDomain1.solarArray)
        sunSaverRepository.addSolarArray(TestSolarArrayDomain2.solarArray)

        val list = sunSaverRepository.getAllSolarArrays().first()
        assertEquals(2, list.size)

        sunSaverRepository.deleteSolarArray(list[1])
        assertEquals(1, fakeDatasource.getSavedSolarArrays().size)

        sunSaverRepository.deleteSolarArray(list[0])
        assertEquals(0, fakeDatasource.getSavedSolarArrays().size)
    }

    @Test
    fun updateSolarArray() = runTest {
        sunSaverRepository.addSolarArray(TestSolarArrayDomain1.solarArray)

        val solarArrayList = sunSaverRepository.getAllSolarArrays().first()
        assertEquals(1, solarArrayList.size)

        val solarArray = solarArrayList[0] // get the solar array with needed ids
        val updatedRoofSections = listOf(
            solarArray.roofSections[0],
            RoofSection(
                id = null,
                area = 12.3,
                incline = 40.0,
                direction = 0.0,
                panels = 2,
                mapId = null,
            )
        )
        val updatedSolarArray = solarArray.copy(
            name = "updated", // updates in solar array
            roofSections = updatedRoofSections
        )

        sunSaverRepository.updateSolarArray(updatedSolarArray)

        val savedSolarArrayList = fakeDatasource.getSavedSolarArrays()
        assertEquals(1, savedSolarArrayList.size)

        val savedSolarArray = savedSolarArrayList[0]
        // test if solar array id persisted
        assertEquals(solarArray.id, savedSolarArray.solarArray.id)
        assertEquals("updated", savedSolarArray.solarArray.name)
        assertEquals(2, savedSolarArray.roofSections.size)
        assert(savedSolarArray.roofSections.find { it.incline == 40.0 } != null)
        assert(savedSolarArray.roofSections.find { it.panels == 4 } == null)
    }
}

class FakeSunSaverDatasource: ISunSaverDatasource {
    private var solarArrayList = mutableListOf<SolarArrayWithRoofSections>()
    private val _solarArrayFlow = MutableStateFlow<List<SolarArrayWithRoofSections>>(emptyList())
    private var idCounterSolarArray = 1L
    private var idCounterRoofSection = 1L

    override suspend fun insertSolarArrayWithRoofSections(
        solarArrayWithRoofSections: SolarArrayWithRoofSections
    ): Long {

        val roofSectionsWithIds = solarArrayWithRoofSections.roofSections.map {
            it.copy(solarArrayId = idCounterSolarArray, roofSectionId = idCounterRoofSection++)
        }

        val solarArrayWithId = solarArrayWithRoofSections.copy(
            solarArray = solarArrayWithRoofSections.solarArray.copy(
                id = idCounterSolarArray
            ),
            roofSections = roofSectionsWithIds
        )

        solarArrayList.add(solarArrayWithId)
        _solarArrayFlow.value = solarArrayList.toList()
        idCounterSolarArray++

        return solarArrayWithId.solarArray.id
    }

    override fun getAllSolarArrays(): Flow<List<SolarArrayWithRoofSections>> {
        return _solarArrayFlow
    }

    override suspend fun delete(solarArrayWithRoofSections: SolarArrayWithRoofSections) {
        solarArrayList.removeIf { it.solarArray.id == solarArrayWithRoofSections.solarArray.id }
        _solarArrayFlow.value = solarArrayList.toList()
    }

    override suspend fun update(solarArrayWithRoofSections: SolarArrayWithRoofSections) {
        val index = solarArrayList.indexOfFirst {
            it.solarArray.id == solarArrayWithRoofSections.solarArray.id
        }

        if (index != -1) {
            solarArrayList[index] = solarArrayWithRoofSections
            _solarArrayFlow.value = solarArrayList.toList()  // Emit updated copy
        } else { // shouldn't happen in reality
            error("Update failed: SolarArray not found")
        }
    }

    // just for testing, doesn't exist in actual datasource
    // we have to check mapping, but the getAllSolarArrays will just map back to a SolarArray
    fun getSavedSolarArrays(): List<SolarArrayWithRoofSections> {
        return solarArrayList.toList()
    }
}