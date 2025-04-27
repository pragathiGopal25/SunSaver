package no.uio.ifi.in2000.team54

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team54.data.shared.SunSaverDatasource
import no.uio.ifi.in2000.team54.database.RoofSectionEntity
import no.uio.ifi.in2000.team54.database.SolarArrayEntity
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.database.SunSaverDao
import no.uio.ifi.in2000.team54.database.SunSaverDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SolarArrayDatasourceTest {
    private lateinit var sunSaverDao: SunSaverDao
    private lateinit var sunSaverDatabase: SunSaverDatabase
    private lateinit var sunSaverDatasource: SunSaverDatasource

    @Before // do this before each test
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()

        sunSaverDatabase = Room.inMemoryDatabaseBuilder(context, SunSaverDatabase::class.java)
            .allowMainThreadQueries() // just for testing
            .build()
        sunSaverDao = sunSaverDatabase.sunSaverDao()
        sunSaverDatasource = SunSaverDatasource(sunSaverDao)
    }

    @After // clean up
    @Throws(IOException::class)
    fun closeDatabase() {
        sunSaverDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetASolarPanel() = runTest  {
        val testData: SolarArrayWithRoofSections = TestSolarArray1.solarArrayWithRoofSections
        // insert and get
        val id: Long = sunSaverDatasource.insert(testData)
        val result: SolarArrayWithRoofSections = sunSaverDatasource.getAllSolarArrays().first()[0]

        // assert correct id
        assertEquals(result.solarArray.id, id)
        // check if other fields are as expected
        assertEqualsSolarArray(testData.solarArray, result.solarArray)

        val roofSections = result.roofSections.sortedBy { it.mapId }
        assertEquals(2, roofSections.size)

        // we know that test roof sections are sorted by mapId (and they have a mapid)
        val startRoofSections = testData.roofSections
        assertEqualsRoofSection(startRoofSections[0], roofSections[0], id)
        assertEqualsRoofSection(startRoofSections[1], roofSections[1], id)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDelete() = runTest {
        val testData1 = TestSolarArray1.solarArrayWithRoofSections
        val testData2 = TestSolarArray2.solarArrayWithRoofSections

        sunSaverDatasource.insert(testData1)
        sunSaverDatasource.insert(testData2)

        val result1 = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(2, result1.size)
        val toDelete = result1.find {
            it.solarArray.name == testData1.solarArray.name
        }!!
        sunSaverDatasource.delete(toDelete)

        val result2 = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(1, result2.size)
        assert(result2.find { it.solarArray.name == testData1.solarArray.name } == null)
        assert(result2.find { it.solarArray.name == testData2.solarArray.name } != null)

        sunSaverDatasource.delete(result2[0])
        assertEquals(0, sunSaverDatasource.getAllSolarArrays().first().size)
    }

    // update is more tricky, so more tests on it
    @Test
    @Throws(Exception::class)
    fun updateASolarPanel() = runTest  { // test where only a solar panel is updated
        val testData = TestSolarArray1.solarArrayWithRoofSections

        // insert the actual one
        val id = sunSaverDatasource.insert(testData)
        val resultBefore = sunSaverDatasource.getAllSolarArrays().first()[0]
        assertEquals("Premium", resultBefore.solarArray.panelType) // small check

        // create an updated solar array
        val updatedSolarArray = resultBefore.solarArray.copy(panelType = "Economy")
        val updatedTestData = resultBefore.copy(solarArray = updatedSolarArray)

        sunSaverDatasource.update(updatedTestData)

        val resultAfterList = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(1, resultAfterList.size) // didn't add anything
        assertEquals("Economy", resultAfterList[0].solarArray.panelType) // update succeed

        // roof sections shouldn't be changed
        val roofSectionsBefore = resultBefore.roofSections.sortedBy { it.mapId }
        val roofSectionsAfter = resultAfterList[0].roofSections.sortedBy { it.mapId }
        assertEqualsRoofSection(roofSectionsAfter[0], roofSectionsBefore[0], id)
        assertEqualsRoofSection(roofSectionsAfter[1], roofSectionsBefore[1], id)
    }

    // will test if a roof section is updated correctly
    @Test
    @Throws(Exception::class)
    fun updateARoofSection() = runTest  {
        val testData = TestSolarArray2.solarArrayWithRoofSections

        // insert the actual
        val id = sunSaverDatasource.insert(testData)
        val resultBeforeList = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(1, resultBeforeList.size) // doing this to avoid error
        val resultBefore = resultBeforeList[0]
        val roofSectionsBefore = resultBefore.roofSections.sortedBy { it.mapId }

        // verify if no unexpected changes
        assertEquals(2, roofSectionsBefore.size)
        assertEqualsRoofSection(testData.roofSections[0], roofSectionsBefore[0], id)
        assertEqualsRoofSection(testData.roofSections[1], roofSectionsBefore[1], id)

        // create updated
        val updatedRoofSection = roofSectionsBefore[0].copy(panels = 10)
        val updatedTestData = resultBefore // update the first roof
            .copy(roofSections = listOf(updatedRoofSection, roofSectionsBefore[1]))

        sunSaverDatasource.update(updatedTestData)

        // assert
        val resultAfterList = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(1, resultAfterList.size)
        val resultAfter = resultAfterList[0]

        // solar array shouldn't be changed
        assertEqualsSolarArray(testData.solarArray, resultAfter.solarArray)

        // only the first roof section should be changed
        val roofSectionsAfter = resultAfter.roofSections.sortedBy { it.mapId }
        assertEquals(10, roofSectionsAfter[0].panels)
        assertEqualsRoofSection(roofSectionsBefore[1], roofSectionsAfter[1], id)
    }

    @Test
    @Throws(Exception::class)
    fun updateRoofSectionsAdd() = runTest  {
        val testData = TestSolarArray1.solarArrayWithRoofSections

        // insert
        val id = sunSaverDatasource.insert(testData)
        val resultBeforeList = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(1, resultBeforeList.size)

        val resultBefore = resultBeforeList[0]
        val roofSectionsBefore = resultBefore.roofSections.sortedBy { it.mapId }

        // small checks
        assertEqualsSolarArray(testData.solarArray, resultBefore.solarArray)
        assertEquals(2, roofSectionsBefore.size)
        assertEqualsRoofSection(testData.roofSections[0], roofSectionsBefore[0], id)
        assertEqualsRoofSection(testData.roofSections[1], roofSectionsBefore[1], id)

        // update by adding a new roof section
        val newRoofSection = RoofSectionEntity(
            solarArrayId = 0,
            area = 20.0,
            incline = 15.0,
            direction = 203.0,
            panels = 8,
            mapId = "4",
        )
        val updatedRoofSections = roofSectionsBefore.toMutableList()
        updatedRoofSections.add(newRoofSection)

        val updatedTestData = resultBefore.copy(roofSections = updatedRoofSections)
        assertEquals(3, updatedTestData.roofSections.size)

        sunSaverDatasource.update(updatedTestData)

        val resultAfterAddedList = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(1, resultAfterAddedList.size)

        // solar array not changed
        assertEquals(resultAfterAddedList[0].solarArray, resultBefore.solarArray)
        // check if the new one is added
        val roofSectionsAfterAdded = resultAfterAddedList[0].roofSections
        assertEquals(3, roofSectionsAfterAdded.size)
        assert(roofSectionsAfterAdded.find { it.mapId == newRoofSection.mapId } != null)
    }

    @Test
    @Throws(Exception::class)
    fun updateRoofSectionsDelete() = runTest {
        val testData = TestSolarArray1.solarArrayWithRoofSections

        // insert
        val id = sunSaverDatasource.insert(testData)
        val resultBeforeList = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(1, resultBeforeList.size)
        val resultBefore = resultBeforeList[0]
        val roofSectionsBefore = resultBefore.roofSections.sortedBy { it.mapId }
        // small checks
        assertEqualsSolarArray(testData.solarArray, resultBefore.solarArray)
        assertEquals(2, roofSectionsBefore.size)
        assertEqualsRoofSection(testData.roofSections[0], roofSectionsBefore[0], id)
        assertEqualsRoofSection(testData.roofSections[1], roofSectionsBefore[1], id)

        // update by deleting a roof section
        val updatedTestData = resultBefore.copy(
            roofSections = listOf(roofSectionsBefore[1]) // delete the first one
        )

        sunSaverDatasource.update(updatedTestData)

        val resultAfterList = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(resultAfterList.size, 1)

        // again, solar array shouldn't be changed, but roof sections amount should be reduced
        val resultAfter = resultAfterList[0]
        assertEquals(resultBefore.solarArray, resultAfter.solarArray)
        assertEquals(1, resultAfter.roofSections.size)
        assertEqualsRoofSection(roofSectionsBefore[1], resultAfter.roofSections[0], id)
    }

    private fun assertEqualsSolarArray( // ignoring id-field
        expected: SolarArrayEntity,
        actual: SolarArrayEntity,
    ) {
        assertEquals(expected.name, actual.name)
        assertEquals(expected.panelType, actual.panelType)
        assertEquals(expected.latitude, actual.latitude, 0.0)
        assertEquals(expected.longitude, actual.longitude, 0.0)
        assertEquals(expected.powerConsumption, actual.powerConsumption, 0.0)
    }

    private fun assertEqualsRoofSection(
        expected: RoofSectionEntity,
        actual: RoofSectionEntity,
        solarArrayId: Long,
    ) {
        assertEquals(solarArrayId, actual.solarArrayId)
        assertEquals(expected.area, actual.area, 0.0)
        assertEquals(expected.incline, actual.incline, 0.0)
        assertEquals(expected.direction, actual.direction, 0.0)
        assertEquals(expected.panels, actual.panels)
        assertEquals(expected.mapId, actual.mapId)
    }
}