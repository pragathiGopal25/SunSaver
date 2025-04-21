package no.uio.ifi.in2000.team54

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team54.data.shared.SunSaverDatasource
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
    fun insertAndGetASolarPanel() = runBlocking {
        val id: Long = sunSaverDatasource.insertSolarArrayWithRoofSections(TestSolarArray1.solarArrayWithRoofSections)
        val solarArray: SolarArrayWithRoofSections = sunSaverDatasource.getAllSolarArrays()[0]

        assertEquals(solarArray.solarArray.id, id)
        val updatedSolarArray = updateId(TestSolarArray1.solarArrayWithRoofSections, id).solarArray
        assertEquals(solarArray.solarArray, updatedSolarArray)

        val roofSections = solarArray.roofSections.sortedBy { it.mapId }
        assertEquals(roofSections.size, 2)
        assertEquals(roofSections[0].solarArrayId, solarArray.solarArray.id)
        assertEquals(roofSections[0].direction, 120.0, 0.0)
        assertEquals(roofSections[1].panels, 2)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDelete() = runBlocking {
        val id1 = sunSaverDatasource.insertSolarArrayWithRoofSections(TestSolarArray1.solarArrayWithRoofSections)
        val id2 = sunSaverDatasource.insertSolarArrayWithRoofSections(TestSolarArray2.solarArrayWithRoofSections)
        assertEquals(sunSaverDatasource.getAllSolarArrays().size, 2)

        sunSaverDatasource.delete(updateId(TestSolarArray1.solarArrayWithRoofSections, id1))
        assertEquals(sunSaverDatasource.getAllSolarArrays().size, 1)

        sunSaverDatasource.delete(updateId(TestSolarArray2.solarArrayWithRoofSections, id2))
        assertEquals(sunSaverDatasource.getAllSolarArrays().size, 0)
    }

    // update is more tricky, so more testing
    @Test
    @Throws(Exception::class)
    fun updateTheSolarPanel() = runBlocking { // test where only the solar panel is updated

        // insert the actual one
        val id = sunSaverDatasource
            .insertSolarArrayWithRoofSections(TestSolarArray1.solarArrayWithRoofSections)
        val resultBefore = sunSaverDatasource.getAllSolarArrays().first()
        assertEquals(resultBefore.solarArray.panelType, "Premium") // small check

        // create an updated solar array
        val updatedSolarArray = TestSolarArray1.solarArray.copy(panelType = "Economy")
        val updatedParameter = TestSolarArray1
            .solarArrayWithRoofSections.copy(solarArray = updatedSolarArray)

        sunSaverDatasource.update(updateId(updatedParameter, id))

        val resultAfter = sunSaverDatasource.getAllSolarArrays()
        assertEquals(resultAfter.size, 1) // didn't add anything
        assertEquals(resultAfter.first().solarArray.panelType, "Economy") // update succeed

        // roof sections shouldn't be changed
        val roofSectionsBefore = resultBefore.roofSections.sortedBy { it.mapId }
        val roofSectionsAfter = resultAfter.first().roofSections.sortedBy { it.mapId }
        // avoiding to check ids since they are changed from original test data
        assertEquals(roofSectionsBefore[0].mapId, roofSectionsAfter[0].mapId)
        assertEquals(roofSectionsBefore[1].panels, roofSectionsAfter[1].panels)
    }

    // will test if a roof section is updated correctly
    @Test
    @Throws(Exception::class)
    fun updateARoofSection() = runBlocking {
        // insert the actual
        val id = sunSaverDatasource
            .insertSolarArrayWithRoofSections(TestSolarArray2.solarArrayWithRoofSections)
        val resultBeforeList = sunSaverDatasource.getAllSolarArrays()
        assertEquals(resultBeforeList.size, 1) // do this to avoid error
        val resultBefore = resultBeforeList.first()
        val roofSectionsBefore = resultBefore.roofSections.sortedBy { it.mapId }
        // small checks
        assertEquals(roofSectionsBefore.size, 2)
        assertEquals(roofSectionsBefore[0].panels, 6)

        // create updated
        val updatedRoofSection = roofSectionsBefore[0].copy(panels = 10)
        val updatedParameter = TestSolarArray2 // update the first roof
            .solarArrayWithRoofSections
            .copy(roofSections = listOf(updatedRoofSection, roofSectionsBefore[1]))

        sunSaverDatasource.update(updateId(updatedParameter, id))

        // assert
        val resultAfterList = sunSaverDatasource.getAllSolarArrays()
        assertEquals(resultAfterList.size, 1)
        val resultAfter = resultAfterList.first()

        // solar array shouldn't be changed
        assertEquals(
            updateId(TestSolarArray2.solarArrayWithRoofSections, id).solarArray,
            resultAfter.solarArray
        )

        // only the first roof section should be changed
        val roofSectionsAfter = resultAfter.roofSections.sortedBy { it.mapId }
        assertEquals(roofSectionsAfter[0].panels, 10)
        assertEquals(roofSectionsAfter[1].area, roofSectionsBefore[1].area, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun updateRoofSectionsAdd() = runBlocking {
        val id = sunSaverDatasource
            .insertSolarArrayWithRoofSections(TestSolarArray1.solarArrayWithRoofSections)
        val resultBeforeList = sunSaverDatasource.getAllSolarArrays()
        assertEquals(resultBeforeList.size, 1)
        val resultBefore = resultBeforeList.first()
        val roofSectionsBefore = resultBefore.roofSections.sortedBy { it.mapId }
        // small checks
        assertEquals(roofSectionsBefore.size, 2)
        assertEquals(roofSectionsBefore[0].panels, 9)

        val newRoofSection = TestSolarArray2.solarArrayWithRoofSections.roofSections[0]
        val updatedRoofSections = roofSectionsBefore.toMutableList()
        updatedRoofSections.add(newRoofSection)

        val updatedParameter = TestSolarArray1 // update roofs
            .solarArrayWithRoofSections
            .copy(roofSections = updatedRoofSections)

        sunSaverDatasource.update(updateId(updatedParameter, id))

        val resultAfterAddedList = sunSaverDatasource.getAllSolarArrays()
        assertEquals(resultAfterAddedList.size, 1)
        // solar array not changed
        assertEquals(resultAfterAddedList.first().solarArray, resultBefore.solarArray)

        // check if the new one is added
        val roofSectionsAfterAdded = resultAfterAddedList.first().roofSections
        assertEquals(roofSectionsAfterAdded.size, 3)
        assert(roofSectionsAfterAdded.find { it.mapId == newRoofSection.mapId } != null)
    }

    @Test
    @Throws(Exception::class)
    fun updateRoofSectionsDelete() = runBlocking {
        // add
        val id = sunSaverDatasource
            .insertSolarArrayWithRoofSections(TestSolarArray1.solarArrayWithRoofSections)
        val resultBeforeList = sunSaverDatasource.getAllSolarArrays()
        assertEquals(resultBeforeList.size, 1)
        val resultBefore = resultBeforeList.first()
        val roofSectionsBefore = resultBefore.roofSections.sortedBy { it.mapId }
        // small checks
        assertEquals(roofSectionsBefore.size, 2)
        assertEquals(roofSectionsBefore[0].panels, 9)
        assertEquals(roofSectionsBefore[1].panels, 2)

        // update by deleting a roof section
        val updatedParameter = TestSolarArray1 // update roofs
            .solarArrayWithRoofSections
            .copy(roofSections = listOf(roofSectionsBefore[1])) // delete the first one

        sunSaverDatasource.update(updateId(updatedParameter, id))

        val resultAfterList = sunSaverDatasource.getAllSolarArrays()
        assertEquals(resultAfterList.size, 1)

        // again, solar array should be changed, but roof sections amount should be reduced
        val resultAfter = resultAfterList.first()
        assertEquals(resultBefore.solarArray, resultAfter.solarArray)
        assertEquals(resultAfter.roofSections.size, 1)
        assertEquals(resultAfter.roofSections.first().mapId, roofSectionsBefore[1].mapId)
    }

    private fun updateId ( // after insertion
    solarArrayWithRoofSections: SolarArrayWithRoofSections,
    id: Long
    ): SolarArrayWithRoofSections {
        return solarArrayWithRoofSections.copy( // make a copy with copied solarArray
            solarArray = solarArrayWithRoofSections.solarArray.copy( // make a copy of solar array
                id = id
            )
        )
    }
}